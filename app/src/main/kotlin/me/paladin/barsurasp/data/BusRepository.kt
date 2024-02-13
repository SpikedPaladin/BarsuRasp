package me.paladin.barsurasp.data

import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.BusLoader
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.models.BusInfo
import me.paladin.barsurasp.utils.loadFromFile
import me.paladin.barsurasp.utils.saveToFile
import java.io.File
import java.util.Calendar

object BusRepository {

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
            info = loadFromFile<BusInfo>(file)
        } else {
            info = BusLoader.loadBusInfo(direction, progressCallback)

            saveToFile(file, info)
        }
        return info
    }

    suspend fun getBusDirections(useCache: Boolean = true): List<BusDirection> {
        val directions: List<BusDirection>
        val file = getDirectionsFile()
        if (file.exists() && useCache) {
            directions = loadFromFile<BusDirection.Wrapper>(file).directions
        } else {
            directions = BusLoader.loadBuses()

            saveToFile(file, BusDirection.Wrapper(Calendar.getInstance().time.toString(), directions))
        }

        return directions
    }

    fun isLoaded() = getDirectionsFile().exists()

    fun BusDirection.isLoaded() = getBusFile(busNumber).exists()

    private fun getBusFile(number: Int) = File(App.getCacheDir(), "app/buses/$number.json")
    private fun getDirectionsFile() = File(App.getCacheDir(), "app/buses/directions.json")
}