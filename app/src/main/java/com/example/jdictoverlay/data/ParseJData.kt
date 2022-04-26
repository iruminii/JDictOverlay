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
        inputStream.close()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readFeed(parser: XmlPullParser) : List<DictEntry> {
        var entries = mutableListOf<DictEntry>()
        // while not end of document
        while(parser.eventType != XmlPullParser.END_DOCUMENT) {
            Log.d("Hi", "readfeed before when eventtype = "+ parser.eventType +
                    "\nparser.name = " + parser.name)
            // switch based on event type
            when (parser.eventType) {
                // start tag then read entry
                XmlPullParser.START_TAG -> {
                    if(parser.name == "JMdict") {
                        Log.d("Hi", "parser.name = " + parser.name)
                        entries = readEntries(parser)
                    } else { continue }
                }
                else -> { Log.d("Hi", "readfeed else parser.eventType = " + parser.eventType)
                    skip(parser) }
            }
        }
        Log.d("Hi", "readfeed done parser.eventtype = " + parser.eventType)
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntries(parser: XmlPullParser): MutableList<DictEntry> {
        val entries = mutableListOf<DictEntry>()
        Log.d("Hi", "In readentries parser.name = " + parser.name)
        parser.require(XmlPullParser.START_TAG, ns, "JMdict")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                Log.d("Hi", "readEntries, eventType = " + parser.eventType
                        + "\nparser.name = " + parser.name)
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "entry") {
                Log.d("Hi", "readEntries, eventType = " + parser.eventType
                        + "\n parser.name = " + parser.name)

                entries.add(readEntry(parser))

            } else {
                Log.d("Hi", "readEntries else, eventType = " + parser.eventType
                        + "\n parser.name = " + parser.name)

                skip(parser)
            }
        }
        return entries
    }
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser) : DictEntry {
        Log.d("Hi", "readEntry, eventType = " + parser.eventType
                + "\n parser.name = " + parser.name)

        var id: String? = null
        var kanji = mutableListOf<String>()
        var reading = mutableListOf<String>()
        var entrySense: EntrySense? = null
        // requires start tag of entry
        parser.require(XmlPullParser.START_TAG, ns, "entry")
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.d("Hi", "readEntry not end tag")
            // if its not a start tag
            if (parser.eventType != XmlPullParser.START_TAG) {
                Log.d("Hi", "readEntry, not start eventType = " + parser.eventType
                        + "\n parser.name = " + parser.name)

                continue
            }
            Log.d("Hi", "readEntry, not end eventType = " + parser.eventType
                    + "\n parser.name = " + parser.name)

            when (parser.name) {
                ENT_SEQ -> {         Log.d("Hi", "readEntry, ENTSEQ eventType = " + parser.eventType
                        + "\n parser.name = " + parser.name)
                    id = readId(parser) }
                K_ELE -> { val entryKanji = readKeb(parser).toString()
                    kanji.add(entryKanji) }
                R_ELE -> {         Log.d("Hi", "readEntry, RELE eventType = " + parser.eventType
                        + "\n parser.name = " + parser.name)
                    val entryReading = readReb(parser).toString()
                    reading.add(entryReading)
                    Log.d("Hi", "readEntryTEST reading = " + reading[0].toString())}
                SENSE -> { Log.d("Hi", "readEntry start event SENSE")
                    entrySense = readSense(parser) }
                else -> {skip(parser)}
            }
        }
        Log.d("Hi", "readEntryTEST id = " + id + "\nPOS = " + entrySense?.entryPos?.get(0) +
                "\nEXTEXT = " + entrySense?.entryExample?.entryExt)

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
        Log.d("Hi", "readKeb ")
        var keb = ""
        parser.require(XmlPullParser.START_TAG, ns, K_ELE)
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.d("Hi", "readKeb not end event")

            if (parser.eventType != XmlPullParser.START_TAG) {
                Log.d("Hi", "readKeb not start event ")

                continue
            }
            when (parser.name) {
                KEB -> {
                    Log.d("Hi", "readKeb start event is KEB")

                    parser.require(XmlPullParser.START_TAG, ns, KEB)
                    //val keb = readText(parser)
                    keb = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, KEB)
                }
                else -> skip(parser)
            }
        }
        Log.d("Hi", "finish keb")

        //parser.require(XmlPullParser.END_TAG, ns, K_ELE)
        return keb
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readReb(parser: XmlPullParser) : String {
        Log.d("Hi", "readReb, eventType = " + parser.eventType
                + "\n parser.name = " + parser.name)

        parser.require(XmlPullParser.START_TAG, ns, R_ELE)
        var reb = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.d("Hi", "parser.eventType = " + parser.eventType +
                    ", parser.name = " + parser.name + ", parser.text =" + parser.text)
            if (parser.eventType != XmlPullParser.START_TAG) {
                Log.d("Hi", "readReb, not start eventType = " + parser.eventType
                        + "\n parser.text = " + parser.text.toString())
                continue
            }
            when (parser.name) {
                REB -> {
                    Log.d("Hi", "readreb REB, eventType = " + parser.eventType
                            + "\n parser.name = " + parser.name)
                    parser.require(XmlPullParser.START_TAG, ns, REB)
                    Log.d("Hi", "was it a start reb tag + parser.type = " + parser.eventType)
                    //val reb = readText(parser)
                    reb = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, REB)
                }
                else -> skip(parser)
            }
        }

        Log.d("Hi", "readREB reb = " + reb.toString())
        //parser.require(XmlPullParser.END_TAG, ns, R_ELE)
        return reb
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readSense(parser: XmlPullParser) : EntrySense {
        Log.d("Hi", "Readsense")
        Log.d("Hi", "Readsense after start tag")
        var pos = mutableListOf<String>()
        var dial = ""
        val gloss = mutableListOf<String>()
        var entryExample = EntryExample("", "", "")

        //var sense : EntrySense ?= null
        //var sense : EntrySense ?= EntrySense(mutableListOf(""), "", mutableListOf(""),
        //    EntryExample("", "", ""))
        parser.require(XmlPullParser.START_TAG, ns, SENSE)
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.d("Hi", "Readsense parser not end event" +
                    "\nparser.event = " + parser.eventType)

            if (parser.eventType != XmlPullParser.START_TAG) {
                Log.d("Hi", "Readsense parser not start event" +
                        "\nparser.event = " + parser.eventType +
                        ", parser.text = " + parser.text + ", parser.name = " + parser.name)
                continue
            }

            when (parser.name) {
                POS -> {
                    Log.d("Hi", "Readsense parser POS" +
                            "\nparser.event = " + parser.eventType)

                    parser.require(XmlPullParser.START_TAG, ns, POS)
                    val entrySense = readText(parser)

                    pos.add(entrySense)
                    parser.require(XmlPullParser.END_TAG, ns, POS)
                }
                DIAL -> {
                    Log.d("Hi", "Readsense parser DIAL" +
                            "\nparser.event = " + parser.eventType)
                    parser.require(XmlPullParser.START_TAG, ns, DIAL)
                    //val dial = readText(parser)
                    dial = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, DIAL)
                }
                GLOSS -> {

                    Log.d("Hi", "Readsense parser GLOSS" +
                            "\nparser.event = " + parser.eventType)
                    parser.require(XmlPullParser.START_TAG, ns, GLOSS)
                    val entryGloss = readText(parser)
                    gloss.add(entryGloss)
                    parser.require(XmlPullParser.END_TAG, ns, GLOSS)
                }
                EXAMPLE -> {
                    Log.d("Hi", "Readsense parser EXAMPLE" +
                            "\nparser.event = " + parser.eventType)
                    //val ex = readExample(parser) }
                    entryExample = readExample(parser) }
                else -> {
                    Log.d("Hi", "Readsense parser ELSE" +
                            "\nparser.event = " + parser.eventType)
                    skip(parser) }
            }
        }
        Log.d("Hi", "readSense outside while")
        return EntrySense(pos, dial, gloss, entryExample) ?: EntrySense(mutableListOf(""), "", mutableListOf(""),
            EntryExample("", "", "")
        )
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readExample(parser: XmlPullParser) : EntryExample {
        Log.d("Hi", "read example")
        parser.require(XmlPullParser.START_TAG, ns, EXAMPLE)
        var ext = ""
        var exj = ""
        var exe = ""
        //var example = EntryExample("", "", "")
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.d("Hi", "read example not end event")
            if (parser.eventType != XmlPullParser.START_TAG) {
                Log.d("Hi", "read example not start event")
                continue
            }
            when (parser.name) {
                EX_TEXT -> {Log.d("Hi", "read example EX TEXT")
                    parser.require(XmlPullParser.START_TAG, ns, EX_TEXT)
                    //val ex = readText(parser)
                    ext = readText(parser)
                    parser.require(XmlPullParser.END_TAG, ns, EX_TEXT)
                }
                EX_SENT -> { Log.d("Hi", "read example EX SENTENCE")
                    parser.require(XmlPullParser.START_TAG, ns, EX_SENT)
                    when(parser.getAttributeValue(0)) {
                        EX_SENT_J -> { Log.d("Hi", "read example EX SENT J")
                            //val ex = readText(parser) }
                            exj = readText(parser) }
                        EX_SENT_E -> { Log.d("Hi", "read example EX SENT E")
                            //val ex = readText(parser) }
                            exe = readText(parser) }
                        else -> skip(parser)
                    }
                    //parser.require(XmlPullParser.END_TAG, ns, EX_SENT)

                }
                else -> skip(parser)
            }
        }
        //parser.require(XmlPullParser.END_TAG, ns, EXAMPLE)
        return EntryExample(ext,exj,exe) ?: EntryExample("", "", "")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readText(parser: XmlPullParser) : String {
        var result = ""
        // if next tag is text tag get the text value
        // and go to next start tag
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text

            Log.d("Hi", "readText, eventType = " + parser.eventType
                    + "\n parser.text = " + result)

            parser.nextTag()
            //Log.d("Hi", "readText, nextTag = " + tmp)

        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        Log.d("Hi", "In skip eventTpye = " + parser.eventType)
        when(parser.eventType) {
            // keeps skipping until
            // end tag of the start tag that put us in here
            XmlPullParser.START_TAG -> {

                Log.d("Hi", "In skip starteventtype eventType = " + parser.eventType)
                var depth = 1
                while (depth != 0) {
                    when (parser.next()) {
                        XmlPullParser.END_TAG -> depth--
                        XmlPullParser.START_TAG -> depth++
                    }
                }
            }
            XmlPullParser.END_DOCUMENT -> {
                Log.d("Hi", "SKIP FUNCTION WHEN TYPE IS ENDDOC")
                parser.nextToken()
            }
            else -> { Log.d("Hi", "In skip else eventType = " + parser.eventType)
                while(parser.eventType != XmlPullParser.START_TAG &&
                    parser.eventType != XmlPullParser.END_DOCUMENT) {
                    parser.nextToken()
                    Log.d("Hi", "skip parser.eventType = " + parser.eventType)
                }
            }
        }
    }
}