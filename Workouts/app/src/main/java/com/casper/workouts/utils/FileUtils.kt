package com.casper.workouts.utils

import android.content.Context
import android.net.Uri
import java.io.File

class FileUtils {
    /**
     * Returns name of the file being inserted
     */
    public fun saveWorkoutImage(context: Context, path: String) : String {
        val targetDir = getWorkoutImageFolder(context)
        val originalFile = File(path)
        val targetFile = File(targetDir, originalFile.name)
        originalFile.copyTo(targetFile, true)
        return targetFile.absolutePath
    }

    /**
     * Returns File from filesDir
     */
    public fun getWorkoutImage(context: Context, fileName: String) : File? {
        val imageDirectory = getWorkoutImageFolder(context)
        val imageFile = File(imageDirectory, fileName)
        if (imageFile.exists())
            return imageFile

        return null
    }

    public fun isLocalFile(path: String): Boolean {
        return !path.startsWith("http")
    }

    private fun getWorkoutImageFolder(context: Context) : File {
        val path = File(context.filesDir, "Workout Images")
        if (!path.exists())
            path.mkdir()

        return path
    }
}