package com.example.chitchatapp

class LottieStickers {
    companion object {
        val stickers = listOf(
            R.raw.sticker_among_us,
            R.raw.sticker_anime_1,
            R.raw.sticker_anime_2,
            R.raw.sticker_anime_3,
            R.raw.sticker_anime_4,
            R.raw.sticker_anime_5,
            R.raw.sticker_astromania,
            R.raw.sticker_dancing_1,
            R.raw.sticker_dancing_2,
            R.raw.sticker_dancing_3,
            R.raw.sticker_dancing_dog,
            R.raw.sticker_emoji_1,
            R.raw.sticker_emoji_2,
            R.raw.sticker_emoji_3,
            R.raw.sticker_emoji_4,
            R.raw.sticker_emoji_5,
            R.raw.sticker_emoji_6,
            R.raw.sticker_ghost_1,
            R.raw.sticker_ghost_2,
            R.raw.sticker_ghost_3,
            R.raw.sticker_kakashi,
            R.raw.sticker_monkey_1,
            R.raw.sticker_monkey_2,
            R.raw.sticker_monkey_3,
            R.raw.sticker_monkey_4,
            R.raw.sticker_owl_1,
            R.raw.sticker_owl_2,
            R.raw.sticker_shark_1,
            R.raw.sticker_shark_2,
            R.raw.sticker_shark_3,
            R.raw.sticker_shark_4,
            R.raw.sticker_shark_5,
            R.raw.sticker_shark_6,
            R.raw.sticker_skull_1,
            R.raw.sticker_skull_2,
            R.raw.sticker_squirrel_1,
            R.raw.sticker_squirrel_2,
            R.raw.sticker_squirrel_3,
            R.raw.sticker_yoda_1,
            R.raw.sticker_yoda_2,
            R.raw.sticker_yoda_3,
            R.raw.sticker_yoda_4,
        )

        fun getSticker(index: Int): Int {
            return stickers[index]
        }
    }
}