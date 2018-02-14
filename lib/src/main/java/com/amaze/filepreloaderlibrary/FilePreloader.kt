package com.amaze.filepreloaderlibrary

import kotlinx.coroutines.experimental.runBlocking
import java.io.File

/**
 * Use this class to interact with the library.
 */
object FilePreloader {

    /**
     * Asynchly preload every subfolder in this [path] (exept '.'),
     * the [instantiator] is used to create the `[D]: DataContainer` objects.
     */
    fun <D: DataContainer>preloadFrom(path: String, instantiator: (String) -> D) {
        Processor.workFrom(ProcessUnit(path, instantiator))
    }

    /**
     * Asynchly preload folder (denoted by its [path]),
     * the [instantiator] is used to create the `[D]: DataContainer` objects
     */
    fun <D: DataContainer>preload(path: String, instatiator: (String) -> D) {
        Processor.work(ProcessUnit(path, instatiator))
    }

    /**
     * Get the loaded data, this will load the data in the current thread if it's not loaded.
     *
     * @see preload
     */
    fun <D: DataContainer>load(path: String, instatiator: (String) -> D, getList: (List<D>) -> Unit) {
        runBlocking {
            val t: Pair<Boolean, List<DataContainer>>? = Processor.getLoaded(path)

            if (t != null && t.first) getList(t.second as List<D>)
            else {
                var path = path
                if (!path.endsWith(DIVIDER)) path += DIVIDER
                getList(File(path).list().map { instatiator.invoke(path + it) })
            }
        }
    }

    /**
     * *This function is only to test what data is being preloaded.*
     * Get all the loaded data, this will load the data in the current thread if it's not loaded.
     */
    fun <D: DataContainer>getAllDataLoaded(getList: (List<D>?) -> Unit) {
        runBlocking {
            val preloaded = Processor.getAllData()

            if (preloaded != null && preloaded.isNotEmpty()) getList(preloaded as List<D>)//todo fix
            else getList(null)
        }
    }
}

