package fiuba.matchapp.utils.clickToSelectEditText;

public class Item implements Listable {

    String mylabel;

    public Item(String label){
        mylabel = label;
    }
    @Override
    public String getLabel(){
        return mylabel;
    }
}
