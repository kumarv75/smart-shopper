package app.shopper;

import java.util.Iterator;
import java.util.TreeSet;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.LinearLayout;

@Root
public class ItemList {
	@ElementList
	private TreeSet<Item> itemList =new TreeSet<Item>();
	private shopper parent;
	
	public shopper getParent() {
		return parent;
	}

	public void setParent(shopper parent) {
		this.parent = parent;
	}

	public ItemList(shopper shopper) {
		super();
		parent = shopper;
	}
	//for simple xml
	public ItemList() {
		super();
	}
	
	public TreeSet<Item> getItemList() {
		return itemList;
	}

	public void setItemList(TreeSet<Item> itemList) {
		this.itemList = itemList;
	}
	
	public void addItem(Item item) {
		itemList.add(item);
	}
	
	public void deleteItem(Item item){
		itemList.remove(item);
	}
	
	public void deleteItem(String name){
		deleteItem(search(name));
	}
	
	public void deleteItem(long id){
		if(id==0)return;
		Iterator<Item> iterator = itemList.iterator();
		for (int i=1;i<=id;i++)
			iterator.next();
		iterator.remove();
	}	
	
	public long search(CharSequence text) {
		Iterator<Item> iterator = itemList.iterator();
		int index = 1;
		while(iterator.hasNext()){
			if(iterator.next().getName().compareToIgnoreCase((String) text)==0 )
				return index;
			index++;
		}
		shopper.debug("Unable to find" + text);
		return 0;
	}
	
	public View display(boolean shoppingList) {
		LinearLayout ll = new LinearLayout(shopper.con);
		ll.setOrientation(LinearLayout.VERTICAL);
		Iterator<Item> iterator = itemList.iterator();
		Item item= new Item(null);
		while(iterator.hasNext()){
			item = iterator.next();
			if(item.isValid()){
				View v = item.draw(shoppingList);
				if(v!=null)
					ll.addView(v);
				if(shoppingList==false)
					parent.registerForContextMenu(v);
			}else
				iterator.remove();
		}
        return ll;
	}

	@Deprecated 
	public void loadItemList(SharedPreferences settings){
	       int count = settings.getInt("item_count", 0),index =1;
	       if( count ==0) return;
	       shopper.debug(count + " records");
	       while(index<=count){
	    	   String itemCode = "item_"+index;

	    	   Item item = new Item(null);
	    	   item.load(itemCode,settings); 	  
	    	   
	    	   itemList.add(item);
	    	   index++;
	       }
	}
	
	@Deprecated 
	public void saveItemList(Editor editor){			
		Iterator<Item> iterator = itemList.iterator();
		int index = 1;
		while(iterator.hasNext()){
			Item item = iterator.next();
			if(!item.isValid())continue;
			String itemCode = "item_"+index;
			
			item.store(itemCode,editor);
			
			index++;
		}
		editor.putInt("item_count", index-1);
		// Commit the edits!
		shopper.debug("Saved "+ (index-1)+" records");
		editor.commit();	
	}
}
