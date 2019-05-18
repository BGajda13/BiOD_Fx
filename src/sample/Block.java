package sample;

public class Block {
    byte[] chars;

    public byte[] getChars() {
        return chars;
    }

    public Block(int size, byte[] charsArr){
        chars = new byte[size];
        for (int i = 0; i < charsArr.length; i++) {
            this.chars[i] = charsArr[i];
        }

    }
}
