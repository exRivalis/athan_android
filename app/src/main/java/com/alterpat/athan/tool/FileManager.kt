package com.alterpat.athan.tool

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.*

class FileManager {


    companion object {

        private val TAG: String = "FileManager"
        private const val filename = "userConf.txt"
        private const val filepath = "athanStorage"
        var myFile: File? = null

        fun saveConf(context: Context, content:String){
            myFile =  File(context.getExternalFilesDir(filepath), filename)
            var fos =  FileOutputStream(myFile, false)
            fos.write(content.toByteArray())
            fos.close();
        }

        fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        fun isExternalStorageReadable(): Boolean {
            return Environment.getExternalStorageState() in
                    setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
        }

        fun write(content: String){
            if (isExternalStorageWritable()) {
                FileOutputStream(File(filename), false).use { output ->
                    output.write(content.toByteArray())
                    output.close()
                }
            }
        }

        fun read() : String {
            if(isExternalStorageReadable()){
                val stringBuilder = StringBuilder()
                var fis =  FileInputStream(filename)

                BufferedReader(InputStreamReader(fis)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }

                val res = stringBuilder.toString()
                Log.d(TAG, res)

                return res
            }

            return ""
        }


        @Throws(IOException::class)
        fun readConf(context: Context): String {
            val contentResolver = context.contentResolver
            val stringBuilder = StringBuilder()
            myFile =  File(context.applicationContext.getExternalFilesDir(filepath), filename)
            var fos =  FileInputStream(myFile)

            BufferedReader(InputStreamReader(fos)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }

            val res = stringBuilder.toString()
            Log.d(TAG, res)

            return res
        }


        /*
        fun saveConf(context: Context, uri: Uri, content: String) {
            try {
                val contentResolver = context.contentResolver

                contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use {
                        it.write(
                            content.toByteArray()
                        )
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
         */
    }
}