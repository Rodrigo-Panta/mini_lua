package interpreter.value;

import java.util.Map;

import interpreter.util.Utils;

public class TableValue extends Value<Map<Value<?>, Value<?>>> {

    private Map<Value<?>, Value<?>> value;

    public TableValue(Map<Value<?>, Value<?>> value) {
        this.value = value;
    }

    @Override
    public Map<Value<?>, Value<?>> value() {
        return this.value;
    }

    @Override
    public boolean eval() {
        return true;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TableValue) {
            return this.value == ((TableValue) obj).value;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");

        for (Map.Entry<Value<?>, Value<?>> e : this.value.entrySet()) {
            Value<?> k = e.getKey();
            Value<?> v = e.getValue();
            String vStr = v == null? "nil" : v.toString();

            sb.append(" [");
            sb.append(k.toString());
            sb.append("] = ");
            sb.append(vStr.toString());
            sb.append(",");
        }

        if (sb.length() > 1)
            sb.setLength(sb.length() - 1);

        sb.append(" }");
        return sb.toString();
    }
}
