package com.maanraj514.utils

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * This class handles the files, load, copy etc.
 */
class FileUtil {
    /*
     * Logger for logging messages.
     */
    private val logger = Bukkit.getLogger()

    /**
     * Loads the file specified.
     * Returns the file loaded.
     *
     * @param plugin the main plugin instance
     * @param resource the file to be loaded
     * @return the file loaded
     */
    fun loadFile(plugin: JavaPlugin, resource: String): File {
        val folder = plugin.dataFolder
        if (!folder.exists()) if (!folder.mkdir()) {
            logger.severe("FAILED TO MAKE DIRECTORY " + folder.name)
        }
        val resourceFile = File(folder, resource)
        try {
            if (!resourceFile.exists()) {
                if (!resourceFile.createNewFile()) {
                    logger.severe("FAILED TO CREATE A NEW FILE " + resourceFile.name)
                }
                plugin.getResource(resource).use { `in` ->
                    Files.newOutputStream(resourceFile.toPath()).use { out ->
                        if (`in` != null) {
                            ByteStreams.copy(`in`, out)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resourceFile
    }

    /**
     * Copies the source and pastes it in the destination.
     *
     * @param source the file to be copied
     * @param destination the destination the file will be pasted in
     */
    @Throws(IOException::class)
    fun copy(source: File, destination: File) {
        if (source.isDirectory) {
            if (!destination.exists()) {
                if (!destination.mkdir()) {
                    logger.severe("FAILED TO MAKE DIRECTORY " + destination.name)
                }
            }
            val files = source.list() ?: return
            for (file in files) {
                val newSource = File(source, file)
                val newDestination = File(destination, file)
                copy(newSource, newDestination)
            }
        } else {
            val `in` = Files.newInputStream(source.toPath())
            val out = Files.newOutputStream(destination.toPath())
            val buffer = ByteArray(1024)
            var length: Int
            while (`in`.read(buffer).also { length = it } > 0) {
                out.write(buffer, 0, length)
            }
            `in`.close()
            out.close()
        }
    }

    /**
     * Deletes the file specified.
     *
     * @param file the file to be deleted
     */
    fun delete(file: File) {
        if (file.isDirectory) {
            val files = file.listFiles() ?: return
            for (child in files) {
                delete(child)
            }
        }
        if (!file.delete()) {
            logger.severe("FAILED TO DELETE FILE " + file.name)
        }
    }
}