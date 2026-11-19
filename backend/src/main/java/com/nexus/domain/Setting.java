package com.nexus.domain; import jakarta.persistence.*;
@Entity @Table(name="nexus_settings") public class Setting{@Id public String id; public String settingValue; protected Setting(){} public Setting(String id,String value){this.id=id;settingValue=value;}}
