package com.example.babygrowthtracker.data.repository

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.repository.PdfRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PdfRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfRepository {
    override suspend fun generatePdf(logs: List<LogEntry>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        paint.textSize = 20f
        canvas.drawText("Baby Growth Report", 10f, 25f, paint)

        paint.textSize = 12f
        var yPosition = 60f
        logs.forEach { log ->
            canvas.drawText("Timestamp: ${log.timestamp}, Value: ${log.value}", 10f, yPosition, paint)
            yPosition += 20
        }

        document.finishPage(page)

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "BabyGrowthReport.pdf")
        try {
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}