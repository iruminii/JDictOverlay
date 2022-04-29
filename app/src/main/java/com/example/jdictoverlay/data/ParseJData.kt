package com.example.jdictoverlay.data

import android.util.Log
import android.util.Xml
import com.example.jdictoverlay.model.DictEntry
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

private val ns: String? = null


private const val ENT_SEQ = "ent_seq"
private const val K_ELE = "k_ele"
private const val KEB = "keb"
private const val R_ELE = "r_ele"
private const val SENSE = "sense"
private const val REB = "reb"
private const val POS = "pos"
private const val DIAL = "dial"
private const val GLOSS = "gloss"
private const val EXAMPLE = "example"
private const val EX_TEXT = "ex_text"
private const val EX_SENT = "ex_sent"
private const val EX_SENT_J = "jpn"
private const val EX_SENT_E = "eng"


class ParseJData {
    data class EntrySense (
        val entryPos: MutableList<String>,
        var entryDial: String,
        val entryGloss: MutableList<String>,
        var entryExample: EntryExample)

    data class EntryExample (
        var entryExt: String,
        var entryExJ: String,
        var entryExE: String
    )

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<DictEntry> {

        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, true)
            parser.setInput(inputStream, null)
            parser.nextToken()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readFeed(parser: XmlPullParser) : List<DictEntry> {
        var entries = mutableListOf<DictEntry>()
        // while not end of document
        while(parser.eventType != XmlPullParser.END_DOCUMENT) {
            // switch based on event type
            when (parser.eventType) {
                // start tag then read entry
                XmlPullParser.START_TAG -> {
                    if(parser.name == "JMdict") {
                        entries = readEntries(parser)
                    } else { continue }
                }
                else -> { skip(parser) }
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntries(parser: XmlPullParser): MutableList<DictEntry> {
        val entries = mutableListOf<DictEntry>()
        parser.require(XmlPullParser.START_TAG, ns, "JMdict")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "entry") {
                entries.add(readEntry(parser))

            } else {
                skip(parser)
            }
        }
        return entries
    }
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser) : DictEntry {
        var id: String? = null
        var kanji = mutableListOf<String>()
        var reading = mutableListOf<String>()
        var entrySense: EntrySense? = null
        // requires start tag of entry
        parser.require(XmlPullParser.START_TAG, ns, "entry")
        while (parser.next() != XmlPullParser.END_TAG) {
            // if its not a start tag
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                ENT_SEQ -> { id = readId(parser) }
                K_ELE -> { val entryKanji = readKeb(parser).toString()
                            kanji.add(entryKanji) }
                R_ELE -> { val entryReading = readReb(parser).toString()
                            reading.add(entryReading) }
                SENSE -> { entrySense = readSense(parser) }
                else -> {skip(parser)}
            }
        }
        return DictEntry(id ?: "", kanji ?: listOf(""), reading?: listOf(""),
            entrySense?.entryPos ?: listOf(""), entrySense?.entryDial ?: "", entrySense?.entryGloss ?: listOf(""),
            entrySense?.entryExample?.entryExt ?: "", entrySense?.entryExample?.entryExJ ?:"", entrySense?.entryExample?.entryExE ?:"")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readId(parser: XmlPullParser) : String {
        parser.require(XmlPullParser.START_TAG, ns, ENT_SEQ)
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, ENT_SEQ)
        return id
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readKeb(parser: XmlPullParser) : String {
        var keb = ""
        parser.require(XmlPullParser.START_TAG, ns, K_ELE)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                KEB -> {
                    parser.require(XmlPullParser.START_TAG, ns, KEB)
                    keb = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, KEB)
                }
                else -> skip(parser)
            }
        }
        return keb
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readReb(parser: XmlPullParser) : String {
        parser.require(XmlPullParser.START_TAG, ns, R_ELE)
        var reb = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                REB -> {
                    parser.require(XmlPullParser.START_TAG, ns, REB)
                    reb = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, REB)
                }
                else -> skip(parser)
            }
        }

        return reb
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readSense(parser: XmlPullParser) : EntrySense {
        var pos = mutableListOf<String>()
        var dial = ""
        val gloss = mutableListOf<String>()
        var entryExample = EntryExample("", "", "")

        parser.require(XmlPullParser.START_TAG, ns, SENSE)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                POS -> {
                    parser.require(XmlPullParser.START_TAG, ns, POS)
                    val entrySense = readText(parser)

                    pos.add(entrySense)
                    parser.require(XmlPullParser.END_TAG, ns, POS)
                }
                DIAL -> {
                    parser.require(XmlPullParser.START_TAG, ns, DIAL)
                    dial = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, DIAL)
                }
                GLOSS -> {
                    parser.require(XmlPullParser.START_TAG, ns, GLOSS)
                    val entryGloss = readText(parser)
                    gloss.add(entryGloss)
                    parser.require(XmlPullParser.END_TAG, ns, GLOSS)
                }
                EXAMPLE -> {
                    entryExample = readExample(parser) }
                else -> { skip(parser) }
            }
        }
        return EntrySense(pos, dial, gloss, entryExample) ?: EntrySense(mutableListOf(""), "", mutableListOf(""),
            EntryExample("", "", "")
        )
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readExample(parser: XmlPullParser) : EntryExample {
        parser.require(XmlPullParser.START_TAG, ns, EXAMPLE)
        var ext = ""
        var exj = ""
        var exe = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                EX_TEXT -> { parser.require(XmlPullParser.START_TAG, ns, EX_TEXT)
                    ext = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, EX_TEXT)
                }
                EX_SENT -> { parser.require(XmlPullParser.START_TAG, ns, EX_SENT)
                    when(parser.getAttributeValue(0)) {
                        EX_SENT_J -> { exj = readText(parser) }
                        EX_SENT_E -> { exe = readText(parser) }
                        else -> skip(parser)
                    }
                }
                else -> skip(parser)
            }
        }
        return EntryExample(ext,exj,exe) ?: EntryExample("", "", "")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readText(parser: XmlPullParser) : String {
        var result = ""
        // if next tag is text tag get the text value
        // and go to next start tag
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        when(parser.eventType) {
            // keeps skipping until
            // end tag of the start tag that put us in here
            XmlPullParser.START_TAG -> {
                var depth = 1
                while (depth != 0) {
                    when (parser.next()) {
                        XmlPullParser.END_TAG -> depth--
                        XmlPullParser.START_TAG -> depth++
                    }
                }
            }
            XmlPullParser.END_DOCUMENT -> {
                parser.nextToken()
            }
            else -> {
                while(parser.eventType != XmlPullParser.START_TAG &&
                    parser.eventType != XmlPullParser.END_DOCUMENT) {
                    parser.nextToken()
                }
            }
        }
    }
}