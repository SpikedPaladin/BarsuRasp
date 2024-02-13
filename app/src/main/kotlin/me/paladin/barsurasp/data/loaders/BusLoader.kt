package me.paladin.barsurasp.data.loaders

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.paladin.barsurasp.App
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.models.BusInfo
import me.paladin.barsurasp.models.BusStop
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Calendar

object BusLoader {

    suspend fun loadBuses(): List<BusDirection> = withContext(Dispatchers.IO) {
        val mainPage: String = App.client.get("https://barautopark.by/services/freight/5/").body()
        val doc = Jsoup.parse(mainPage)

        val links = doc.getElementsByTag("tbody")[0].getElementsByTag("a")

        val busDirections = mutableListOf<BusDirection>()
        var prevBus = 0
        for (link in links) {
            val (name, number, id) = link.parseBusDirection()

            if (number == prevBus) {
                busDirections.last().apply {
                    backwardName = name
                    backward = id
                }
            } else {
                busDirections += BusDirection(
                    busNumber = number,
                    name = name,
                    forward = id
                )
            }
            prevBus = number
        }

        return@withContext busDirections
    }

    suspend fun loadBusInfo(busDirection: BusDirection, progressCallback: (Float) -> Unit): BusInfo {
        val forwardIds = loadBusStopsIds(busDirection.forward)!!
        val backwardIds = loadBusStopsIds(busDirection.backward)

        val size = forwardIds.size + (backwardIds?.size ?: 0)
        var loadedSize = 0

        val progressUpdate: () -> Unit = {
            loadedSize++
            progressCallback(loadedSize.toFloat() / size)
        }

        return BusInfo(
            lastFetch = Calendar.getInstance().time.toString(),
            name = busDirection.name,
            backwardName = busDirection.backwardName,
            number = busDirection.busNumber,
            stops = loadBusStops(forwardIds, progressUpdate)!!,
            backwardStops = loadBusStops(backwardIds, progressUpdate)
        )
    }

    private suspend fun loadBusStopsIds(directionId: Int?): List<Int>? = withContext(Dispatchers.IO) {
        if (directionId == null)
            return@withContext null

        val busDirPage: String = App.client.get("https://barautopark.by/services/freight/$directionId/?print=y").body()
        val doc = Jsoup.parse(busDirPage)

        val ids = mutableListOf<Int>()
        val stopsElement = doc
            .getElementsByClass("tabs-category")[0]
            .getElementsByTag("div")

        for (element in stopsElement) {
            if (element.attr("class") != "d")
                continue

            ids += element.attr("id").toInt()
        }
        return@withContext ids
    }

    private suspend fun loadBusStops(ids: List<Int>?, loadedCallback: () -> Unit): List<BusStop>? {
        if (ids == null)
            return null

        val stops = mutableListOf<BusStop>()
        for (id in ids) {
            stops += loadBusStop(id)
            loadedCallback()
        }

        return stops
    }

    private suspend fun loadBusStop(stopId: Int): BusStop = withContext(Dispatchers.IO) {
        val stopPage: String = App.client.get("https://barautopark.by/bitrix/templates/barautopark/ajax.php?action=getBusPath&element_id=$stopId").body()
        val doc = Jsoup.parse(stopPage)

        BusStop(
            doc.getElementsByTag("h3")[0].text(),
            doc.getElementsByTag("tbody")
        )
    }

    private fun Element.parseBusDirection(): Triple<String, Int, Int> {
        val name = text().substring(text().indexOf(" ") + 1)
        val number = text().split(" ")[0].replace("№", "").toInt()
        val id = attr("href").split("/")[3].toInt()

        return Triple(name, number, id)
    }
}