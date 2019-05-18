package sample;

public class CircularBuffer {
    private class Element{
        byte value;
        Element next;

        public Element(byte val, Element next){
            value = val;
            this.next = next;
        }
        public void setNext(Element next){
            this.next = next;
        }

        public Element getNext() {
            return next;
        }

        public byte getValue() {
            return value;
        }

        @Override
        protected Element clone(){
            return new Element(value,next);
        }
    }

    private Element head,oriHead;

    public CircularBuffer(byte[] array){
        head = new Element(array[0],null);
        Element tempElement = new Element(array[1],null);
        head.setNext(tempElement);
        oriHead = head.clone();
        for (int i = 2; i < array.length; i++) {
            Element temp2 = new Element(array[i],null);
            tempElement.setNext(temp2);
            tempElement = temp2;
        }
        tempElement.setNext(head);
        System.out.println();
    }
    public byte get(){
        byte ret = head.getValue();
        head = head.getNext();
        return ret;
    }
    public void reset(){
        head = oriHead;
    }

}
