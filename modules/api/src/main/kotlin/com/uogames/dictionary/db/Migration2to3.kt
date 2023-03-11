package com.uogames.dictionary.db

import com.uogames.dictinary.v3.db.entity.*
import com.uogames.dictionary.db.entity.v2.*
import com.uogames.dictionary.db.entity.v2.CardEntity
import com.uogames.dictionary.db.entity.v2.ImageEntity
import com.uogames.dictionary.db.entity.v2.ModuleEntity
import com.uogames.dictionary.db.entity.v2.PhraseEntity
import com.uogames.dictionary.db.entity.v2.PronunciationEntity
import com.uogames.dictionary.db.entity.v2.UserEntity
import com.uogames.dictionary.db.entity.v2.ModuleCardEntity
import com.uogames.dictionary.service.ifNull
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Migration2to3 {

    fun dropV2() = transaction {
        SchemaUtils.drop(
            ModuleCards,
            Modules,
            Cards,
            Phrases,
            Pronunciations,
            Images,
            Users
        )
    }

    fun createDBv3() = transaction {
        SchemaUtils.create(
            Config,
            UserTable,
            ImageTable,
            PronunciationTable,
            PhraseTable,
            CardTable,
            ModuleTable,
            ModuleCardTable,
            ReportTable
        )
        ConfigEntity.findById("version").ifNull {
            ConfigEntity.new("version") { value = "3" }
        }
    }

    fun dropV3() = transaction {
        SchemaUtils.drop(
            ReportTable,
            ModuleCardTable,
            ModuleTable,
            CardTable,
            PhraseTable,
            PronunciationTable,
            ImageTable,
            UserTable
        )
    }

    fun migration2to3() = transaction {
        try {
            createDBv3()
            UserEntity.all().forEach{
                com.uogames.dictinary.v3.db.entity.UserEntity.new(it.id.value) {
                    name = it.name
                    strike = 0
                    ban = false
                }
            }
            commit()

            ImageEntity.all().forEach {
                com.uogames.dictinary.v3.db.entity.ImageEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    imageUri = it.imageUri
                    ban = false
                }
            }
            commit()

            PronunciationEntity.all().forEach {
                com.uogames.dictinary.v3.db.entity.PronunciationEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    audioUri = it.audioUri
                    ban = false
                }
            }
            commit()

            PhraseEntity.all().forEach {
                com.uogames.dictinary.v3.db.entity.PhraseEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    phrase = it.phrase
                    definition = it.definition
                    lang = it.lang
                    country = it.country
                    idPronounce = it.idPronounce
                    idImage = it.idImage
                    timeChange = it.timeChange
                    like = it.like
                    dislike = it.dislike
                    ban = false
                }
            }
            commit()

            CardEntity.all().forEach {
                com.uogames.dictinary.v3.db.entity.CardEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    idPhrase = it.idPhrase
                    idTranslate = it.idTranslate
                    idImage = it.idImage
                    reason = it.reason
                    timeChange = it.timeChange
                    like = it.like
                    dislike = it.dislike
                    ban = false
                }
            }
            commit()

            ModuleEntity.all().forEach {
                com.uogames.dictinary.v3.db.entity.ModuleEntity.new(it.id.value) {
                    name = it.name
                    timeChange = it.timeChange
                    like = it.like
                    dislike = it.dislike
                    globalOwner = it.globalOwner
                    ban = false
                }
            }
            commit()

            ModuleCardEntity.all().forEach {
                com.uogames.dictinary.v3.db.entity.ModuleCardEntity.new(it.id.value) {
                    globalOwner = it.globalOwner
                    moduleId = it.moduleId
                    cardId = it.cardId
                }
            }
            commit()

            ConfigEntity.findById("version")?.let { it.value = "3" }
            commit()

            //dropV2()

        } catch (e :Exception){
            dropV3()
            ConfigEntity.findById("version")?.let { it.value = "2" }
            throw e
        }
    }

}