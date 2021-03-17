package com.pocketdocket.print

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.*
import java.lang.Exception
import kotlin.text.Charsets.UTF_8

@RequiresApi(Build.VERSION_CODES.KITKAT)
class PrintAdapter(context: Context, val path: String) : PrintDocumentAdapter() {
    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback?, extras: Bundle?) {
        if (cancellationSignal!!.isCanceled) {
            callback!!.onLayoutCancelled()
        }
        else {
            val builder = PrintDocumentInfo.Builder("docket")
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_UNKNOWN)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()
            callback!!.onLayoutFinished(builder.build(), oldAttributes != newAttributes)
        }
    }

    override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor?, cancellationSignal: CancellationSignal?, callback: WriteResultCallback?) {
        var `in`: InputStream?=null
        var out: OutputStream?=null

        try {
            `in` = FileInputStream(path)
            out = FileOutputStream(destination!!.fileDescriptor)

            if (!cancellationSignal!!.isCanceled) {
                `in`.copyTo(out)
                callback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
            else {
                callback!!.onWriteCancelled()
            }
        }
        catch (e: Exception) {
            callback!!.onWriteFailed(e.message)
            e.message?.let { Log.e("Print error", it) }
        }
        finally {
            try {
                `in`!!.close()
                out!!.close()
            }
            catch (e: IOException) {
                e?.message?.let { Log.e("Failed to close streams", it) }
            }

        }
    }
}