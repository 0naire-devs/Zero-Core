package kr.hyfata.zero.textutil;

public class UniqueText {
    public String makeBoldtext(String text) {
        text = text.replace("1", "\uF801\uF801\uE024");
        text = text.replace("2", "\uF801\uF801\uE025");
        text = text.replace("3", "\uF801\uF801\uE026");
        text = text.replace("4", "\uF801\uF801\uE027");
        text = text.replace("5", "\uF801\uF801\uE028");
        text = text.replace("6", "\uF801\uF801\uE029");
        text = text.replace("7", "\uF801\uF801\uE02A");
        text = text.replace("8", "\uF801\uF801\uE02B");
        text = text.replace("9", "\uF801\uF801\uE02C");
        text = text.replace("0", "\uF801\uF801\uE02D");
        text = text.replace(",", "\uF801\uF801\uE02E");
        return text;
    }
}