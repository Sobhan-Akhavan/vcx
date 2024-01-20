package ir.vcx.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Sobhan on 11/9/2023 - VCX
 */

@Getter
@AllArgsConstructor
public enum GenreType {
    ACTION("اکشن"),
    ADVENTURE("ماجراجویانه"),
    ANIMATION("انیمیشن"),
    COMEDY("کمدی"),
    DEVOTIONAL("فداکارانه"),
    DRAMA("درام"),
    HISTORICAL("تاریخی"),
    HORROR("ترسناک"),
    SCIENCE_FICTION("علمی تخیلی"),
    WESTERN("وسترن");

    private final String value;
}
