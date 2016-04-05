package fiuba.matchapp.utils.clickToSelectEditText;

public class Item implements Listable {

    String label;

    public Item(String label){
        label = label;
    }
    @Override
    public String getLabel(){
        return label;
    }
}
