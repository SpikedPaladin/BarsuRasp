package me.paladin.barsurasp.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.BusLoader
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.models.BusInfo
import java.io.File
import java.util.Calendar

object BusRepository {
    // TODO Remove after 1.1
    private val json = Json { ignoreUnknownKeys = true }

    fun deleteBusInfo(number: Int) {
        val file = getBusFile(number)
        if (file.exists())
            file.delete()
    }

    suspend fun getBusInfo(number: Int, progressCallback: (Float) -> Unit = {}): BusInfo {
        val directions = getBusDirections()
        val direction = directions.find { it.busNumber == number }!!

        val info: BusInfo
        val file = getBusFile(number)
        if (file.exists()) {
            info = json.decodeFromString<BusInfo>(file.readText())
        } else {
            info = BusLoader.loadBusInfo(direction, progressCallback)

            file.parentFile?.mkdirs()
            file.writeText(Json.encodeToString(info))
        }
        return info
    }

    suspend fun getBusDirections(): List<BusDirection> {
        val directions: List<BusDirection>
        val file = getDirectionsFile()
        if (file.exists()) {
            directions = Json.decodeFromString<BusDirection.Wrapper>(file.readText()).directions
        } else {
            directions = BusLoader.loadBuses()

            file.parentFile?.mkdirs()
            file.writeText(Json.encodeToString(BusDirection.Wrapper(Calendar.getInstance().time.toString(), directions)))
        }

        return directions
    }

    fun isLoaded() = getDirectionsFile().exists()

    fun BusDirection.isLoaded() = getBusFile(busNumber).exists()

    private fun getBusFile(number: Int) = File(App.getCacheDir(), "app/buses/$number.json")
    private fun getDirectionsFile() = File(App.getCacheDir(), "app/buses/directions.json")
}