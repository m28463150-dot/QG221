package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.EventEntity
import com.example.data.model.TicketEntity
import com.example.data.model.TrackEntity

object ShareHelper {

    fun shareTrack(context: Context, track: TrackEntity, toWhatsApp: Boolean = false) {
        val shareText = buildString {
            append("🎵 *${track.title}* - ${track.artistName}")
            if (track.feat.isNotBlank()) append(" (feat. ${track.feat})")
            append("\n\n🌊 Découvre ce son sur *QUAY GUETT 221* (Le Quai musical de Guet Ndar, Saint-Louis & Sénégal) !")
            append("\n🎧 Écoute en streaming illimité : https://quayguett221.sn/track/${track.id}")
            append("\n#QuayGuett221 #GuetNdar #MusiqueGalsen #SaintLouis")
        }

        shareTextInternal(context, shareText, "Partager ce morceau", toWhatsApp)
    }

    fun shareEvent(context: Context, event: EventEntity, toWhatsApp: Boolean = false) {
        val shareText = buildString {
            append("🎤 *CONCERT LIVE : ${event.title}*")
            append("\n👤 Artiste : ${event.artistName}")
            append("\n📍 Lieu : ${event.venueName} (${event.city})")
            append("\n📅 Date : ${event.dateTimeText}")
            append("\n\n🎟️ Réservez vos places en 1 clic Wave / Orange Money sur *QUAY GUETT 221* !")
            append("\n🔗 https://quayguett221.sn/event/${event.id}")
            append("\n#QuayGuett221 #LiveGalsen #SaintLouisConcert")
        }

        shareTextInternal(context, shareText, "Partager cet événement", toWhatsApp)
    }

    fun shareTicket(context: Context, ticket: TicketEntity, toWhatsApp: Boolean = false) {
        val shareText = buildString {
            append("🎟️ *MON PASS CONCERT QUAY GUETT 221*")
            append("\nConcert : *${ticket.eventTitle}*")
            append("\nCatégorie : [${ticket.ticketTypeName.uppercase()}]")
            append("\nLieu : ${ticket.venueName}")
            append("\nDate : ${ticket.eventDateText}")
            append("\nCode Billet : ${ticket.id.take(8).uppercase()}")
            append("\n\n⚡ Billet vérifié et sécurisé par QR Code unique. Rendez-vous au Quai !")
        }

        shareTextInternal(context, shareText, "Partager mon billet", toWhatsApp)
    }

    private fun shareTextInternal(context: Context, text: String, title: String, toWhatsApp: Boolean) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, title)
        }

        if (toWhatsApp) {
            sendIntent.setPackage("com.whatsapp")
            try {
                context.startActivity(sendIntent)
                return
            } catch (e: Exception) {
                // If WhatsApp is not installed, fallback to standard chooser
                Toast.makeText(context, "WhatsApp non installé, ouverture des options...", Toast.LENGTH_SHORT).show()
                sendIntent.setPackage(null)
            }
        }

        val chooser = Intent.createChooser(sendIntent, title)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
