package com.aiems.be.modules.hospital.client.response;

import com.aiems.be.modules.hospital.domain.Bed;
import com.aiems.be.modules.hospital.domain.Specialty;
import com.aiems.be.modules.hospital.service.result.BedInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@JacksonXmlRootElement(localName = "response")
public record RealTimeBedInfoResponse(
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
            // 공통 식별/일시
            Integer rnum,
            String hpid,
            String phpid,
            String hvidate,

            // 병상 수(숫자)
            Integer hvec,     // 일반(응급실일반병상)
            Integer hvoc,     // [기타] 수술실
            Integer hvcc,     // [중환자실] 신경과
            Integer hvncc,   // [중환자실] 신생아
            Integer hvccc,   // [중환자실] 흉부외과
            Integer hvicc,   // [중환자실] 일반
            Integer hvgc,     // [입원실] 일반

            // 당직의/연락처/전용 항목
            String hvdnm,    // 당직의
            String hv1,        // 응급실 당직의 직통연락처

            // 장비/가용(Y/N 계열은 String)
            String hvctayn,         // CT가용
            String hvmriayn,       // MRI가용
            String hvangioayn,   // 혈관촬영기가용
            String hvventiayn,   // 인공호흡기가용
            String hvventisoayn, // 인공호흡기 조산아가용 (Y/N1)
            String hvincuayn,     // 인큐베이터가용 (Y/N1)
            String hvcrrtayn,     // CRRT가용 (Y/N1)
            String hvecmoayn,     // ECMO가용 (Y/N1)
            String hvoxyayn,       // 고압산소치료기가용 (Y/N1)
            String hvhypoayn,     // 중심체온조절유도기 (Y/N1)
            String hvamyn,           // 구급차가용여부 (Y/N)

            // 세부 병상/구역
            String hv2,    // [중환자실] 내과
            String hv3,    // [중환자실] 외과
            String hv4,    // 외과입원실(정형외과)
            String hv5,    // 신경과입원실
            String hv6,    // [중환자실] 신경외과
            String hv7,    // 약물중환자
            String hv8,    // [중환자실] 화상
            String hv9,    // [중환자실] 외상
            String hv10,   // VENTI(소아) -> Y/N
            String hv11,   // 인큐베이터(보육기) -> Y/N
            String hv12,   // 소아당직의 직통연락처(문서 예시가 번호이므로 String)
            String hv13,  // 격리진료구역 음압격리병상
            String hv14,  // 격리진료구역 일반격리병상
            String hv15,  // 소아음압격리
            String hv16,  // 소아일반격리
            String hv17,  // [응급전용] 중환자실 음압격리
            String hv18,  // [응급전용] 중환자실 일반격리
            String hv19,  // [응급전용] 입원실 음압격리
            String hv21,  // [응급전용] 입원실 일반격리
            String hv22,  // 감염병 전담병상 중환자실
            String hv23,  // 감염병 전담병상 중환자실 내 음압격리병상
            String hv24,  // [감염] 중증 병상
            String hv25,  // [감염] 준-중증 병상
            String hv26,  // [감염] 중등증 병상
            String hv27,  // 코호트 격리
            String hv28,  // 소아
            String hv29,  // 응급실 음압 격리 병상
            String hv30,  // 응급실 일반 격리 병상
            String hv31,  // [응급전용] 중환자실
            String hv32,  // [중환자실] 소아
            String hv33,  // [응급전용] 소아중환자실
            String hv34,  // [중환자실] 심장내과
            String hv35,  // [중환자실] 음압격리
            String hv36,  // [응급전용] 입원실
            String hv37,  // [응급전용] 소아입원실
            String hv38,  // [입원실] 외상전용
            String hv39,  // [기타] 외상전용 수술실
            String hv40,  // [입원실] 정신과 폐쇄병동
            String hv41,  // [입원실] 음압격리
            String hv42,  // [기타] 분만실
            String hv43,  // [기타] 화상전용처치실
            String hv60,  // 외상소생실
            String hv61,  // 외상환자진료구역

            // 기관 정보
            String dutyName,
            String dutyTel3,

            // 기준 병상 (대문자 태그 → 소문자 필드명, 매핑은 localName으로)
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

            return BedInfo.builder()
                    .bedType(bedType)
                    .availableBedCount(availableBedCount)
                    .totalBedCount(totalBedCount)
                    .build();
        }

        private Integer searchByFieldName(String fieldName) throws NoSuchFieldException, IllegalAccessException, ClassCastException {
            Field field = Item.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            return Integer.valueOf(String.valueOf(field.get(this)));
        }

        public static Item empty() {
            return new Item(
                    // ─────────────── 공통 식별/일시 ───────────────
                    0, null, null, null,

                    // ─────────────── 병상 수(숫자) ───────────────
                    0, 0, 0, 0, 0, 0, 0,

                    // ─────────────── 당직의/연락처/전용 항목 ───────────────
                    null, null,

                    // ─────────────── 장비/가용(Y/N) ───────────────
                    "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N",

                    // ─────────────── 세부 병상/구역 (hv2 ~ hv61) ───────────────
                    "0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",
                    "0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",
                    "0","0","0","0","0","0","0","0","0","0","0","0","0",0,0,0,
                    0,0,  // hv60, hv61

                    // ─────────────── 기관 정보 ───────────────
                    null, null,

                    // ─────────────── 기준 병상 (hvs01 ~ hvs61) ───────────────
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
