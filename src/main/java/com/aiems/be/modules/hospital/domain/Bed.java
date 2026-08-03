package com.aiems.be.modules.hospital.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Bed {

    COMMON_WARD                         ("hvec", "hvs01", "일반병동"),
    INTERNAL_MEDICINE                   ("hv2", "hvs06", "내과 중환자실"),
    PEDIATRICS                          ("hv33", "hvs10", "소아중환자실"),
    NEUROLOGY                           ("hvcc", "hvs11", "신경과 중환자실"),
    PSYCHIATRY                          ("hv40", "hvs24", "정신과 폐쇄병동"),
    GENERAL_SURGERY                     ("hv3", "hvs07", "외과 중환자실"),
    THORACIC_SURGERY                    ("hvccc", "hvs16", "흉부외과 중환자실"),
    NEUROSURGERY                        ("hv6", "hvs12", "신경외과 중환자실"),
    OBSTETRICS_GYNECOLOGY               ("hv42", "hvs26", "분만실");

    private final String availableBedCode;
    private final String totalBedCode;
    private final String bedName;
}
