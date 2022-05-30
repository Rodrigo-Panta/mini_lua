package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import interpreter.util.Memory;
import interpreter.value.NumberValue;
import interpreter.value.TableValue;
import interpreter.value.Value;

public class TableExpr extends Expr {

    private List<TableEntry> table;  
    
    public TableExpr(int line) {
        super(line);
        table = new ArrayList();
    }
    
    @Override
    public Value<?> expr(){
        int index = 1;

        HashMap<Value<?>, Value<?>> tableMap = new HashMap<>();
        for(TableEntry entry : table) {
            if(entry.key == null) {
                entry.key = new ConstExpr(this.getLine(), new NumberValue((double)index));
                index++;
            } else if(entry.key.expr() instanceof NumberValue && (double)entry.key.expr().value()==index) {
                //If the key value is the same as the index, the value of the index can no longer be used.
                index++;
            }           
            tableMap.put(entry.key.expr(), entry.value.expr());
        }
        return new TableValue(tableMap);        
    }

    public void addEntry(TableEntry entry) {
        table.add(entry);
    }


}
