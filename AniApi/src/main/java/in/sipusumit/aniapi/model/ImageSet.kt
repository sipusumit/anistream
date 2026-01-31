package `in`.sipusumit.aniapi.model

/**
 * Represents a set of images in different sizes.
 *
 * UI can choose the best size based on screen / bandwidth.
 */
data class ImageSet(

    /** Small image (list items, thumbnails) */
    val small: String? = null,

    /** Medium image (cards, grids) */
    val medium: String? = null,

    /** Large image (detail screen) */
    val large: String? = null
)