package com.aiems.be.modules.hospital.bed.client;

import com.aiems.be.common.domain.Bed;
import com.aiems.be.common.domain.Specialty;
import com.aiems.be.modules.hospital.bed.service.BedInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@JacksonXmlRootElement(localName = "response")
public record BedInfoResponse(
        Header header,
        Body body
) {
    public record Header(
            String resultCode,
            String resultMsg
    ) {
    }

    public record Body(
            List<Item> items,
            Integer pageNo,
            Integer numOfRows,
            Integer totalCount
    ) {
    }

    public record Item(
            Integer rnum,
            String hpid,
            String phpid,
            String hvidate,

            Integer hvec,
            Integer hvoc,
            Integer hvcc,
            Integer hvncc,
            Integer hvccc,
            Integer hvicc,
            Integer hvgc,

            String hvdnm,
            String hv1,

            String hvctayn,
            String hvmriayn,
            String hvangioayn,
            String hvventiayn,
            String hvventisoayn,
            String hvincuayn,
            String hvcrrtayn,
            String hvecmoayn,
            String hvoxyayn,
            String hvhypoayn,
            String hvamyn,

            String hv2,
            String hv3,
            String hv4,
            String hv5,
            String hv6,
            String hv7,
            String hv8,
            String hv9,
            String hv10,
            String hv11,
            String hv12,
            String hv13,
            String hv14,
            String hv15,
            String hv16,
            String hv17,
            String hv18,
            String hv19,
            String hv21,
            String hv22,
            String hv23,
            String hv24,
            String hv25,
            String hv26,
            String hv27,
            String hv28,
            String hv29,
            String hv30,
            String hv31,
            String hv32,
            String hv33,
            String hv34,
            String hv35,
            String hv36,
            String hv37,
            String hv38,
            String hv39,
            String hv40,
            String hv41,
            String hv42,
            String hv43,
            String hv60,
            String hv61,

            String dutyName,
            String dutyTel3,

            Integer hvs01,
            Integer hvs02,
            Integer hvs03,
            Integer hvs04,
            Integer hvs05,
            Integer hvs06,
            Integer hvs07,
            Integer hvs08,
            Integer hvs09,
            Integer hvs10,
            Integer hvs11,
            Integer hvs12,
            Integer hvs13,
            Integer hvs14,
            Integer hvs15,
            Integer hvs16,
            Integer hvs17,
            Integer hvs18,
            Integer hvs19,
            Integer hvs20,
            Integer hvs21,
            Integer hvs22,
            Integer hvs23,
            Integer hvs24,
            Integer hvs25,
            Integer hvs26,
            Integer hvs27,
            Integer hvs28,
            Integer hvs29,
            Integer hvs30,
            Integer hvs31,
            Integer hvs32,
            Integer hvs33,
            Integer hvs34,
            Integer hvs35,
            Integer hvs36,
            Integer hvs37,
            Integer hvs38,
            Integer hvs46,
            Integer hvs47,
            Integer hvs48,
            Integer hvs49,
            Integer hvs50,
            Integer hvs51,
            Integer hvs52,
            Integer hvs53,
            Integer hvs54,
            Integer hvs55,
            Integer hvs56,
            Integer hvs57,
            Integer hvs58,
            Integer hvs59,
            Integer hvs60,
            Integer hvs61
    ) {
        public BedInfo getBedInfo(Specialty specialty) {
            Bed bed = specialty.getBed();

            String bedType = bed.getBedName();
            Integer availableBedCount = 0;
            Integer totalBedCount = 0;

            try {
                availableBedCount = searchByFieldName(bed.getAvailableBedCode());
                totalBedCount = searchByFieldName(bed.getTotalBedCode());
            } catch (Exception e) {
                try {
                    bedType = Bed.COMMON_WARD.getBedName();
                    availableBedCount = searchByFieldName(Bed.COMMON_WARD.getAvailableBedCode());
                    totalBedCount = searchByFieldName(Bed.COMMON_WARD.getTotalBedCode());
                } catch (Exception ex) {
                    log.warn("병상 정보를 조회하는데 실패하였습니다. [hpid=%s, specialty=%s]".formatted(hpid, specialty), ex);
                }
            }

            return new BedInfo(bedType, totalBedCount, availableBedCount);
        }

        private Integer searchByFieldName(String fieldName) throws NoSuchFieldException, IllegalAccessException, ClassCastException {
            Field field = Item.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            return Integer.valueOf(String.valueOf(field.get(this)));
        }

        public static Item empty() {
            return new Item(
                    0, null, null, null,

                    0, 0, 0, 0, 0, 0, 0,

                    null, null,

                    "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N",

                    "0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",
                    "0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",
                    "0","0","0","0","0","0","0","0","0","0","0","0","0",0,0,0,
                    0,0,

                    null, null,

                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0
            );
        }

    }

    public Map<String, Item> toItemMap() {
        if (body == null || body.items() == null) {
            return Collections.emptyMap();
        }

        return body.items().stream()
                .filter(item -> item.hpid() != null)
                .collect(Collectors.toMap(Item::hpid, item -> item));
    }
}
