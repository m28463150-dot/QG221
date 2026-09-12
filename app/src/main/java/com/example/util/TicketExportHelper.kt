package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.TicketEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.security.MessageDigest

object TicketExportHelper {

    fun generateAndSaveTicketImage(context: Context, ticket: TicketEntity): Uri? {
        val bitmap = renderTicketBitmap(context, ticket)
        val filename = "QuayGuett221_Pass_${ticket.id.take(8).uppercase()}.png"
        var savedUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QuayGuett221")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                    savedUri = uri
                }
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val quayDir = File(picturesDir, "QuayGuett221")
                if (!quayDir.exists()) quayDir.mkdirs()
                val imageFile = File(quayDir, filename)
                val stream: OutputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
                stream.close()
                savedUri = Uri.fromFile(imageFile)
            }

            Toast.makeText(
                context,
                "✅ Pass sauvegardé dans la galerie Photos (/Pictures/QuayGuett221)",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de la sauvegarde: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        return savedUri
    }

    fun shareTicketImage(context: Context, ticket: TicketEntity) {
        try {
            val bitmap = renderTicketBitmap(context, ticket)
            val cachePath = File(context.cacheDir, "tickets")
            cachePath.mkdirs()
            val file = File(cachePath, "ticket_${ticket.id.take(8)}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Pass Concert Quay Guett 221 - ${ticket.eventTitle}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🎟️ Mon Pass Officiel pour « ${ticket.eventTitle} » (${ticket.ticketTypeName}) !\nLieu : ${ticket.venueName}\nÀ présenter à l'entrée du Quai de Guet Ndar."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Partager le Pass Officiel"))
        } catch (e: Exception) {
            // Fallback to text share if FileProvider is not set up
            ShareHelper.shareTicket(context, ticket)
        }
    }

    private fun renderTicketBitmap(context: Context, ticket: TicketEntity): Bitmap {
        val width = 760
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background dark gradient / emerald tone
        val bgPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Header banner: Emerald Green
        val headerPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#0B6E4F")
        }
        canvas.drawRect(0f, 0f, width.toFloat(), 180f, headerPaint)

        // Header text
        val headerTextPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("QUAY GUETT 221", width / 2f, 75f, headerTextPaint)

        val subHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#F4B41A")
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PASS CONCERT OFFICIEL • GUET NDAR (221)", width / 2f, 115f, subHeaderPaint)

        val locationTextPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#DDF3E8")
            textSize = 16f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Saint-Louis, Rufisque & Sénégal", width / 2f, 150f, locationTextPaint)

        // Ticket Details Box
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#1B2A32")
            textSize = 30f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(ticket.eventTitle, width / 2f, 240f, textPaint)

        val venuePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#555555")
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(ticket.venueName, width / 2f, 280f, venuePaint)
        canvas.drawText(ticket.eventDateText, width / 2f, 315f, venuePaint)

        // Category Badge
        val badgeBgPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#F4B41A")
        }
        val badgeRect = RectF(width / 2f - 140f, 345f, width / 2f + 140f, 395f)
        canvas.drawRoundRect(badgeRect, 16f, 16f, badgeBgPaint)

        val badgeTextPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#043324")
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(ticket.ticketTypeName.uppercase(), width / 2f, 380f, badgeTextPaint)

        // QR Code Matrix Area
        val qrTop = 430f
        val qrSize = 360f
        val qrLeft = (width - qrSize) / 2f

        // Draw QR code background
        val qrBgPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#F8F9FA")
        }
        canvas.drawRoundRect(RectF(qrLeft - 20f, qrTop - 20f, qrLeft + qrSize + 20f, qrTop + qrSize + 20f), 20f, 20f, qrBgPaint)

        // Draw deterministic QR matrix
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(ticket.id.toByteArray())
        val sizeCount = 21
        val cellW = qrSize / sizeCount
        val cellH = qrSize / sizeCount

        val qrPaint = Paint().apply {
            isAntiAlias = false
            color = android.graphics.Color.BLACK
        }

        fun drawFinder(startX: Int, startY: Int) {
            canvas.drawRect(qrLeft + startX * cellW, qrTop + startY * cellH, qrLeft + (startX + 7) * cellW, qrTop + (startY + 7) * cellH, qrPaint)
            val whitePaint = Paint().apply { color = android.graphics.Color.WHITE }
            canvas.drawRect(qrLeft + (startX + 1) * cellW, qrTop + (startY + 1) * cellH, qrLeft + (startX + 6) * cellW, qrTop + (startY + 6) * cellH, whitePaint)
            canvas.drawRect(qrLeft + (startX + 2) * cellW, qrTop + (startY + 2) * cellH, qrLeft + (startX + 5) * cellW, qrTop + (startY + 5) * cellH, qrPaint)
        }

        drawFinder(0, 0)
        drawFinder(14, 0)
        drawFinder(0, 14)

        for (y in 0 until sizeCount) {
            for (x in 0 until sizeCount) {
                val inTopLeft = x < 8 && y < 8
                val inTopRight = x > 12 && y < 8
                val inBottomLeft = x < 8 && y > 12
                if (!inTopLeft && !inTopRight && !inBottomLeft) {
                    val bitIndex = (y * sizeCount + x) % (hash.size * 8)
                    val byteVal = hash[bitIndex / 8].toInt()
                    val bit = (byteVal shr (bitIndex % 8)) and 1
                    if (bit == 1) {
                        canvas.drawRect(
                            qrLeft + x * cellW,
                            qrTop + y * cellH,
                            qrLeft + (x + 1) * cellW,
                            qrTop + (y + 1) * cellH,
                            qrPaint
                        )
                    }
                }
            }
        }

        // Ticket ID & Details
        val idPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#333333")
            textSize = 20f
            typeface = Typeface.MONOSPACE
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        canvas.drawText("CODE: ${ticket.id.take(16).uppercase()}", width / 2f, qrTop + qrSize + 60f, idPaint)

        val buyerPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#666666")
            textSize = 18f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Détenteur : ${ticket.buyerPhone}", width / 2f, qrTop + qrSize + 95f, buyerPaint)

        // Status badge
        val statusPaint = Paint().apply {
            isAntiAlias = true
            color = if (ticket.status == "valid") android.graphics.Color.parseColor("#15803D") else android.graphics.Color.RED
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("STATUT : ${ticket.status.uppercase()} • CONTRÔLE VIGILE ACTIF", width / 2f, qrTop + qrSize + 135f, statusPaint)

        // Footer note
        val footerPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#888888")
            textSize = 15f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Présentez cette image ou votre application au vigile.", width / 2f, height - 60f, footerPaint)
        canvas.drawText("« Jekk naa ci Quay bi » • Plateforme Musicale Quay Guett 221", width / 2f, height - 35f, footerPaint)

        return bitmap
    }
}
