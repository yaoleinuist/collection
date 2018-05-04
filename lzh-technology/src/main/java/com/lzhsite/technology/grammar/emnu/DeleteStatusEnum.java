package com.lzhsite.technology.grammar.emnu;

public enum DeleteStatusEnum {
	 /** 
     * 值为0（就是NOT_DELETE）是数据库默认值，未删除； 
     * 值为1（就是HAS_DELETE）是已经被逻辑删除了 
     */  
    NOT_DELETE("未删除","0"),  
    HAS_DELETE("已删除","1");  
      
    private String typeName;  
    private String typeCode;  
      
    private DeleteStatusEnum(String typeName,String typeCode){  
        this.typeName = typeName;  
        this.typeCode = typeCode;  
    }  
  
    public String getTypeName() {  
        return typeName;  
    }  
  
    public void setTypeName(String typeName) {  
        this.typeName = typeName;  
    }  
  
    public String getTypeCode() {  
        return typeCode;  
    }  
  
    public void setTypeCode(String typeCode) {  
        this.typeCode = typeCode;  
    }  
}
