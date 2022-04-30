package com.example.jdictoverlay.ui

import android.util.Log
import java.lang.StringBuilder

//https://gist.github.com/mediavrog/6b13669533ac20d5ba0f

class CharacterConverter {

    fun isFullAlphaNumeric(c: Char) : Boolean {
        return ((('\uff10' <= c) && (c <= '\uff19')) || // digits
                (('\uff21' <= c) && (c <= '\uff3a')) || // uppercase
                (('\uff41' <= c) && (c <= '\uff5a')) ) // lowercase
    }

    fun isNormalAlphaNumeric(c: Char) : Boolean {
        return ((('\u0030' <= c) && (c <= '\u0039')) || // digits
                (('\u0041' <= c) && (c <= '\u005a')) || // uppercase
                (('\u0061' <= c) && (c <= '\u007a')) ) // lowercase
    }

    fun isAlphaNumeric(c: Char) : Boolean {
        return (isNormalAlphaNumeric(c) || isFullAlphaNumeric(c))
    }

    fun isKana(c: Char) : Boolean {
        return (isHiragana(c) || isKatakana(c))
    }

    fun isHiragana(c: Char) : Boolean {
        return (('\u3041' <= c) && (c <= '\u309e'))
    }

    fun isKatakana(c: Char) : Boolean {
        return (isHalfWidthKatakana(c) || isFullWidthKatakana(c))
    }

    fun isHalfWidthKatakana(c: Char) : Boolean {
        return (('\uff66' <= c) && (c <= '\uff9d'))
    }

    fun isFullWidthKatakana(c: Char) : Boolean {
        return (('\u30a1' <= c) && (c <= '\u30fe'))
    }

    fun isKanji(c: Char) : Boolean {
        if ((('\u4e00' <= c) && (c <= '\u9fa5')) ||
            (('\u3005' <= c) && (c <= '\u3007'))) {
            return true
        }
        return false
    }

    fun toFull(c: Char) : Char {
        if (isNormalAlphaNumeric(c)) {
            return (c + 0xfee0).toChar()
        }
        return c
    }

    fun toNormalAlphaNumeric(c: Char) : Char {
        if(isFullAlphaNumeric(c)) {
            return (c - 0xfee0).toChar()
        }
        return c
    }

    fun toKatakana(c: Char) : Char {
        if(isHiragana(c)) {
            return (c+0x60).toChar()
        }
        if(isHalfWidthKatakana(c)) {
            return (halfToFullKata[c]!!.toChar() ?: c)
        }

        return c
    }

    fun toHalfKatakana(c: Char) : Char {
        if(isFullWidthKatakana(c)) {
            Log.d("CHARCONV", "c = $c")
            return (fullToHalfKata[c]?.toChar() ?: c)
        }
        if(isHiragana(c)) {
            return fullToHalfKata[toKatakana(c)]?.toChar() ?: c
        }
        return c
    }

    fun toHiragana(c: Char) : Char {
        if(isFullWidthKatakana(c)) {
            return (c-0x60).toChar()
        }
        if(isHalfWidthKatakana(c)){
            return (halfToFullKata[c]?.minus(0x60))?.toChar() ?: c
        }
        return c
    }

    fun convertToHiragana(input: String) : String {
        if(input == null || input.isEmpty()) {
            return ""
        }
        val output = StringBuilder()

        for(char in input) {
            // if its katakana convert it to hiragana
            if(isKatakana(char)) {
                    output.append(toHiragana(char))
            }
            // if its alphanumeric, convert it to normal width
            else if(isAlphaNumeric(char)) {
                output.append(toNormalAlphaNumeric(char))
            }
            else{
                // if its neither, just append the character as is
                output.append(char)
            }
        }

        return output.toString()
    }

    fun convertToFullKatakana(input: String) : String {
        if(input == null || input.isEmpty()) {
            return ""
        }
        val output = StringBuilder()

        for(char in input) {
            // if its half kata or hira convert it to katagana
            if(isHalfWidthKatakana(char) || isHiragana(char)) {
                output.append(toKatakana(char))
            }
            // if its alphanumeric, convert it to normal width
            else if(isAlphaNumeric(char)) {
                output.append(toNormalAlphaNumeric(char))
            }
            else{
                // if its neither, just append the character as is
                output.append(char)
            }
        }
        return output.toString()
    }

    fun convertToHalfKatakana(input: String) : String {
        if(input == null || input.isEmpty()) {
            return ""
        }
        val output = StringBuilder()

        for(char in input) {
            // if its kata or hira convert it to half katagana
            if(isFullWidthKatakana(char) || isHiragana(char)) {
                output.append(toHalfKatakana(char))
            }
            // if its alphanumeric, convert it to normal width
            else if(isAlphaNumeric(char)) {
                output.append(toNormalAlphaNumeric(char))
            }
            else{
                // if its neither, just append the character as is
                output.append(char)
            }
        }
        return output.toString()
    }
    fun convertToHiraganaFull(input: String) : String {
        if(input == null || input.isEmpty()) {
            return ""
        }
        val output = StringBuilder()

        for(char in input) {
            // if its katakana convert it to hiragana
            if(isKatakana(char)) {
                    output.append(toHiragana(char))
            }
            // if its alphanumeric, convert it to normal width
            else if(isAlphaNumeric(char)) {
                output.append(toFull(char))
            }
            else{
                // if its neither, just append the character as is
                output.append(char)
            }
        }

        return output.toString()
    }

    fun convertToFullKatakanaFull(input: String) : String {
        if(input == null || input.isEmpty()) {
            return ""
        }
        val output = StringBuilder()

        for(char in input) {
            // if its half kata or hira convert it to katagana
            if(isHalfWidthKatakana(char) || isHiragana(char)) {
                output.append(toKatakana(char))
            }
            // if its alphanumeric, convert it to normal width
            else if(isAlphaNumeric(char)) {
                output.append(toFull(char))
            }
            else{
                // if its neither, just append the character as is
                output.append(char)
            }
        }
        return output.toString()
    }

    fun convertToHalfKatakanaFull(input: String) : String {
        if(input == null || input.isEmpty()) {
            return ""
        }
        val output = StringBuilder()

        for(char in input) {
            // if its kata or hira convert it to half katagana
            if(isFullWidthKatakana(char) || isHiragana(char)) {
                output.append(toHalfKatakana(char))
            }
            // if its alphanumeric, convert it to normal width
            else if(isAlphaNumeric(char)) {
                output.append(toFull(char))
            }
            else{
                // if its neither, just append the character as is
                output.append(char)
            }
        }
        return output.toString()
    }

    companion object {

        @JvmField
        val halfToFullKata: Map<Char, Char> = mapOf(
            (0xff66).toChar() to (0x30f2).toChar(),
            (0xff67).toChar() to (0x30a1).toChar(),
            (0xff68).toChar() to (0x30a3).toChar(),
            (0xff69).toChar() to (0x30a5).toChar(),
            (0xff6a).toChar() to (0x30a7).toChar(),
            (0xff6b).toChar() to (0x30a9).toChar(),
            (0xff6c).toChar() to (0x30e3).toChar(),
            (0xff6d).toChar() to (0x30e5).toChar(),
            (0xff6e).toChar() to (0x30e7).toChar(),
            (0xff6f).toChar() to (0x30c3).toChar(),
            (0xff70).toChar() to (0x30fc).toChar(),
            (0xff71).toChar() to (0x30a2).toChar(),
            (0xff72).toChar() to (0x30a4).toChar(),
            (0xff73).toChar() to (0x30a6).toChar(),
            (0xff74).toChar() to (0x30a8).toChar(),
            (0xff75).toChar() to (0x30aa).toChar(),
            (0xff76).toChar() to (0x30ab).toChar(),
            (0xff77).toChar() to (0x30ad).toChar(),
            (0xff78).toChar() to (0x30af).toChar(),
            (0xff79).toChar() to (0x30b1).toChar(),
            (0xff7a).toChar() to (0x30b3).toChar(),
            (0xff7b).toChar() to (0x30b5).toChar(),
            (0xff7c).toChar() to (0x30b7).toChar(),
            (0xff7d).toChar() to (0x30b9).toChar(),
            (0xff7e).toChar() to (0x30bb).toChar(),
            (0xff7f).toChar() to (0x30bd).toChar(),
            (0xff80).toChar() to (0x30bf).toChar(),
            (0xff81).toChar() to (0x30c1).toChar(),
            (0xff82).toChar() to (0x30c4).toChar(),
            (0xff83).toChar() to (0x30c6).toChar(),
            (0xff84).toChar() to (0x30c8).toChar(),
            (0xff85).toChar() to (0x30ca).toChar(),
            (0xff86).toChar() to (0x30cb).toChar(),
            (0xff87).toChar() to (0x30cc).toChar(),
            (0xff88).toChar() to (0x30cd).toChar(),
            (0xff89).toChar() to (0x30ce).toChar(),
            (0xff8a).toChar() to (0x30cf).toChar(),
            (0xff8b).toChar() to (0x30d2).toChar(),
            (0xff8c).toChar() to (0x30d5).toChar(),
            (0xff8d).toChar() to (0x30d8).toChar(),
            (0xff8e).toChar() to (0x30db).toChar(),
            (0xff8f).toChar() to (0x30de).toChar(),
            (0xff90).toChar() to (0x30df).toChar(),
            (0xff91).toChar() to (0x30e0).toChar(),
            (0xff92).toChar() to (0x30e1).toChar(),
            (0xff93).toChar() to (0x30e2).toChar(),
            (0xff94).toChar() to (0x30e4).toChar(),
            (0xff95).toChar() to (0x30e6).toChar(),
            (0xff96).toChar() to (0x30e8).toChar(),
            (0xff97).toChar() to (0x30e9).toChar(),
            (0xff98).toChar() to (0x30ea).toChar(),
            (0xff99).toChar() to (0x30eb).toChar(),
            (0xff9a).toChar() to (0x30ec).toChar(),
            (0xff9b).toChar() to (0x30ed).toChar(),
            (0xff9c).toChar() to (0x30ef).toChar(),
            (0xff9d).toChar() to (0x30f3).toChar(),
            (0xff9e).toChar() to (0x309b).toChar(),
            (0xff9f).toChar() to (0x309c).toChar(),
        )
        //https://stackoverflow.com/questions/45380280/how-to-reverse-a-map-in-kotlin

        @JvmField
        val fullToHalfKata : Map<Char, Char> = halfToFullKata.entries.associateBy({it.value}) { it.key }
    }

}