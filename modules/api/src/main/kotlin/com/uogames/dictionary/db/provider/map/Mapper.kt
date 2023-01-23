package com.uogames.dictionary.db.provider.map

interface Mapper<E, D> {

    fun toDTO(entity: E): D

    fun toEntity(dto: D): E

}