package me.soodo.studyolle.modules.zone.form;

import lombok.Data;
import me.soodo.studyolle.modules.zone.Zone;

@Data
public class ZoneForm {

    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getLocalNameCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("-") + 1);
    }

    public Zone getZone() {
        return Zone.builder()
                .city(this.getCityName())
                .localNameOfCity(this.getLocalNameCity())
                .province(this.getProvinceName()).build();
    }
}
