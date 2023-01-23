package com.uogames.dictionary.db

import com.uogames.dictionary.db.entity.v1.*
import com.uogames.dictionary.service.ifNull
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Migration1to2 {

    fun createDBv1() = transaction {
        SchemaUtils.create(
            Config,
            Users,
            Images,
            Pronunciations,
            Phrases,
            Cards,
            Modules,
            ModuleCards
        )
        ConfigEntity.findById("version")?.let {
            it.value = "1"
        }.ifNull {
            ConfigEntity.new("version") { value = "1" }
        }
    }

    fun dropV1() = transaction {
        SchemaUtils.drop(
            ModuleCards,
            Modules,
            Cards,
            Phrases,
            Images,
            Pronunciations,
            Users,
        )
    }

    fun createDBv2() = transaction {
        SchemaUtils.create(
            Config,
            com.uogames.dictionary.db.entity.v2.Users,
            com.uogames.dictionary.db.entity.v2.Images,
            com.uogames.dictionary.db.entity.v2.Pronunciations,
            com.uogames.dictionary.db.entity.v2.Phrases,
            com.uogames.dictionary.db.entity.v2.Cards,
            com.uogames.dictionary.db.entity.v2.Modules,
            com.uogames.dictionary.db.entity.v2.ModuleCards
        )
        ConfigEntity.findById("version")?.let {
            it.value = "2"
        }.ifNull {
            ConfigEntity.new("version") { value = "2" }
        }
    }

    fun dropV2() = transaction {
        SchemaUtils.drop(
            com.uogames.dictionary.db.entity.v2.ModuleCards,
            com.uogames.dictionary.db.entity.v2.Modules,
            com.uogames.dictionary.db.entity.v2.Cards,
            com.uogames.dictionary.db.entity.v2.Phrases,
            com.uogames.dictionary.db.entity.v2.Images,
            com.uogames.dictionary.db.entity.v2.Pronunciations,
            com.uogames.dictionary.db.entity.v2.Users
        )
    }

    fun migration1to2() = transaction {
        try {
            createDBv2()
            UserEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.UserEntity.new(it.id.value) {
                    name = it.name
                }
            }
            commit()

            ImageEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.ImageEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    imageUri = it.imageUri
                }
            }
            commit()

            PronunciationEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.PronunciationEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    audioUri = it.audioUri
                }
            }
            commit()

            PhraseEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.PhraseEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    phrase = it.phrase
                    definition = it.definition
                    lang = it.lang.split("-")[0]
                    country = it.lang.split("-")[1]
                    idPronounce = it.idPronounce
                    idImage = it.idImage
                    timeChange = it.timeChange
                    like = it.like
                    dislike = it.dislike
                }
            }
            commit()

            CardEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.CardEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    idPhrase = it.idPhrase
                    idTranslate = it.idTranslate
                    idImage = it.idImage
                    reason = it.reason
                    timeChange = it.timeChange
                    like = it.like
                    dislike = it.dislike
                }
            }
            commit()

            ModuleEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.ModuleEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    name = it.name
                    timeChange = it.timeChange
                    like = it.like
                    dislike = it.dislike
                }
            }
            commit()

            ModuleCardEntity.all().forEach {
                com.uogames.dictionary.db.entity.v2.ModuleCardEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    moduleId = it.moduleId
                    cardId = it.cardId
                }
            }
            commit()

            dropV1()

        } catch (e: Exception) {
            dropV2()
            ConfigEntity.findById("version")?.let { it.value = "1" }
            throw e
        }

    }


}