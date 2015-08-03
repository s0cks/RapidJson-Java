package io.github.s0cks.rapidjson;

public interface Value{
    public byte asByte();
    public short asShort();
    public int asInt();
    public long asLong();
    public float asFloat();
    public double asDouble();
    public boolean asBoolean();
    public boolean isNull();
    public char asChar();
    public String asString();
    public String write();
    public Value getValue(String name);
    public Value[] asArray();
    public void setValue(String name, Value v);
    public void addValue(Value v);
}