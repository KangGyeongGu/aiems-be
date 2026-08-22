package com.aiems.be.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Specialty {

    INTERNAL_MEDICINE                   ("내과", Bed.INTERNAL_MEDICINE),
    PEDIATRICS                          ("소아청소년과", Bed.PEDIATRICS),
    NEUROLOGY                           ("신경과", Bed.NEUROLOGY),
    PSYCHIATRY                          ("정신건강의학과", Bed.PSYCHIATRY),
    DERMATOLOGY                         ("피부과", Bed.COMMON_WARD),
    GENERAL_SURGERY                     ("외과", Bed.GENERAL_SURGERY),
    THORACIC_SURGERY                    ("흉부외과", Bed.THORACIC_SURGERY),
    ORTHOPEDICS                         ("정형외과", Bed.COMMON_WARD),
    NEUROSURGERY                        ("신경외과", Bed.NEUROSURGERY),
    PLASTIC_SURGERY                     ("성형외과", Bed.COMMON_WARD),
    OBSTETRICS_GYNECOLOGY               ("산부인과", Bed.OBSTETRICS_GYNECOLOGY),
    OPHTHALMOLOGY                       ("안과", Bed.COMMON_WARD),
    OTORHINOLARYNGOLOGY                 ("이비인후과", Bed.COMMON_WARD),
    UROLOGY                             ("비뇨기과", Bed.COMMON_WARD),
    REHABILITATION_MEDICINE             ("재활의학과", Bed.COMMON_WARD),
    ANESTHESIOLOGY_PAIN                 ("마취통증의학과", Bed.COMMON_WARD),
    RADIOLOGY                           ("영상의학과", Bed.COMMON_WARD),
    RADIATION_ONCOLOGY                  ("치료방사선과", Bed.COMMON_WARD),
    LABORATORY_MEDICINE                 ("임상병리과", Bed.COMMON_WARD),
    PATHOLOGY                           ("병리과", Bed.COMMON_WARD),
    FAMILY_MEDICINE                     ("가정의학과", Bed.COMMON_WARD),
    NUCLEAR_MEDICINE                    ("핵의학과", Bed.COMMON_WARD),
    EMERGENCY_MEDICINE                  ("응급의학과", Bed.COMMON_WARD),
    DENTISTRY                           ("치과", Bed.COMMON_WARD),
    ORAL_MAXILLOFACIAL_SURGERY          ("구강악안면외과", Bed.COMMON_WARD),
    PROSTHODONTICS                      ("치과보철과", Bed.COMMON_WARD),
    CONSERVATIVE_DENTISTRY              ("치과보존과", Bed.COMMON_WARD),
    PERIODONTICS                        ("치주과", Bed.COMMON_WARD),
    ORTHODONTICS                        ("치과교정과", Bed.COMMON_WARD),
    PEDIATRIC_DENTISTRY                 ("소아치과", Bed.COMMON_WARD),
    ORAL_MEDICINE                       ("구강내과", Bed.COMMON_WARD),
    INTEGRATED_DENTISTRY                ("통합치의학과", Bed.COMMON_WARD),
    PREVENTIVE_DENTISTRY                ("예방치과", Bed.COMMON_WARD),
    ORAL_MAXILLOFACIAL_RADIOLOGY        ("구강악안면방사선과", Bed.COMMON_WARD),
    ORAL_PATHOLOGY                      ("구강병리과", Bed.COMMON_WARD),
    DENTOMAXILLOFACIAL_RADIOLOGY        ("영상치의학과", Bed.COMMON_WARD),
    OCCUPATIONAL_ENVIRONMENTAL_MEDICINE ("직업환경의학과", Bed.COMMON_WARD),
    INDUSTRIAL_MEDICINE                 ("산업의학과", Bed.COMMON_WARD),
    PREVENTIVE_MEDICINE                 ("예방의학과", Bed.COMMON_WARD),
    TUBERCULOSIS_MEDICINE               ("결핵과", Bed.COMMON_WARD),

    KOREAN_INTERNAL_MEDICINE            ("한방내과", Bed.COMMON_WARD),
    KOREAN_REHABILITATION_MEDICINE      ("한방재활의학과", Bed.COMMON_WARD),
    KOREAN_NEUROPSYCHIATRY              ("한방신경정신과", Bed.COMMON_WARD),
    KOREAN_OBSTETRICS_GYNECOLOGY        ("한방부인과", Bed.COMMON_WARD),
    KOREAN_PEDIATRICS                   ("한방소아과", Bed.COMMON_WARD),
    KOREAN_EENT_DERMATOLOGY             ("한방안이비인후피부과", Bed.COMMON_WARD),
    ACUPUNCTURE                         ("침구과", Bed.COMMON_WARD),
    CONSTITUTIONAL_MEDICINE             ("사상체질과", Bed.COMMON_WARD);

    private final String name;
    private final Bed bed;
}
