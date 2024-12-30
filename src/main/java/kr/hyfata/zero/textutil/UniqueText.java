package kr.hyfata.zero.textutil;

public class UniqueText {
    public String makeBoldtext(String text) {
        text = text.replace("1", "\uE024\uF801");
        text = text.replace("2", "\uE025\uF801");
        text = text.replace("3", "\uE026\uF801");
        text = text.replace("4", "\uE027\uF801");
        text = text.replace("5", "\uE028\uF801");
        text = text.replace("6", "\uE029\uF801");
        text = text.replace("7", "\uE02A\uF801");
        text = text.replace("8", "\uE02B\uF801");
        text = text.replace("9", "\uE02C\uF801");
        text = text.replace("0", "\uE02D\uF801");
        return text;
    }
}