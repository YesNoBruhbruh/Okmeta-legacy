package com.maanraj514.builder

import com.maanraj514.utils.toColor
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import java.util.*

/**
 * Creates a new instance of
 * entityBuilder.
 *
 * @param entityType the entityType of the entity.
 */
class EntityBuilder(private val entityType: EntityType) {

    // the name of the entity.
    private var name: String? = ""

    // the health of the entity.
    private var health = 10.0

    // the potionEffects that'll be applied to the entity.
    private val potionEffects: MutableList<PotionEffect> = mutableListOf()

    // the attributes that'll be applied to the entity.
    private var attributes: MutableMap<Attribute, Double> = mutableMapOf()

    // if true entity can move, false if not.
    private var canMove = true

    // if true entity can pick up items, false if not.
    private var canPickUpItems = false

    // if true entity is glowing, false if not.
    private var isGlowing = false

    // if true entity has gravity, false if not.
    private var hasGravity = true

    // if true entity is invincible, false if not.
    private var isInvincible = false

    /**
     * Spawns the entity.
     *
     * @param location the location where the entity will spawn.
     * @return the entity.
     */
    fun spawn(location: Location): Entity {
        val entity: Entity = location.world.spawnEntity(location, entityType)
        handlePostSpawn(entity)
        return entity
    }

    /**
     * This handles what happens
     * after the entity spawns.
     *
     * @param entity the entity that spawned.
     */
    private fun handlePostSpawn(entity: Entity) {
        entity.isCustomNameVisible = name == null
        entity.customName = if (name == null) "" else name!!.toColor()
        (entity as? LivingEntity)?.let { handleLivingEntity(it) }
        entity.isGlowing = isGlowing
        entity.setGravity(hasGravity)
        entity.isInvulnerable = isInvincible
    }

    /**
     * This handles if the entity
     * is a livingEntity.
     *
     * @param entity the entity that is a livingEntity.
     */
    private fun handleLivingEntity(entity: Entity) {
        val livingEntity = entity as LivingEntity
        if (attributes.isNotEmpty()) {
            for ((key, value) in attributes) {
                val attribute1 = livingEntity.getAttribute(key)
                if (attribute1 != null) {
                    attribute1.baseValue = value
                }
            }
        }
        livingEntity.health = health
        livingEntity.setAI(canMove)
        livingEntity.canPickupItems = canPickUpItems
        livingEntity.addPotionEffects(potionEffects)
    }

    /**
     * Adds potion effects if the
     * entity is a livingEntity.
     *
     * @param potionEffects the potionEffects.
     */
    fun addPotionEffects(vararg potionEffects: PotionEffect?): EntityBuilder {
        for (potionEffect in potionEffects){
            if (potionEffect != null) {
                this.potionEffects.add(potionEffect)
            }
        }
        return this
    }

    /**
     * Adds an attribute to the
     * entity.
     *
     * @param attribute the attribute to be added.
     * @param baseValue the value of the attribute.
     * @return this.
     */
    fun addAttribute(attribute: Attribute, baseValue: Double): EntityBuilder {
        attributes[attribute] = baseValue
        return this
    }

    /**
     * Adds all the attributes
     * from the hashmap to the
     * entity.
     *
     * @param attributes the hashMap for attributes.
     * @return this.
     */
    fun addAttribute(attributes: HashMap<Attribute, Double>): EntityBuilder {
        for ((key, value) in attributes) {
            if (!this.attributes.containsKey(key)) {
                this.attributes[key] = value
            }
        }
        return this
    }

    /**
     * Replaces the current attributes map
     * with the new one.
     *
     * @param attributes the replacement for the current attributes map.
     * @return this.
     */
    fun setAttributes(attributes: HashMap<Attribute, Double>): EntityBuilder {
        this.attributes = attributes
        return this
    }

    /**
     * Sets the entity to
     * glowing.
     *
     * @param isGlowing if true, entity will glow, false if not.
     * @return this.
     */
    fun setGlowing(isGlowing: Boolean): EntityBuilder {
        this.isGlowing = isGlowing
        return this
    }

    /**
     * Sets the entity to
     * have gravity.
     *
     * @param hasGravity if true, entity will have gravity, false if not.
     * @return this.
     */
    fun setGravity(hasGravity: Boolean): EntityBuilder {
        this.hasGravity = hasGravity
        return this
    }

    /**
     * Sets the entity to
     * be invincible or not.
     *
     * @param isInvincible if true, entity will be invincible, false if not.
     * @return this.
     */
    fun setInvincible(isInvincible: Boolean): EntityBuilder {
        this.isInvincible = isInvincible
        return this
    }

    /**
     * Sets the entity's health.
     *
     * @param health the health of the entity.
     * @return this.
     */
    fun setHealth(health: Double): EntityBuilder {
        this.health = health
        return this
    }

    /**
     * Sets if the entity
     * can move or not.
     *
     * @param canMove if true, entity can move, false if not.
     * @return this.
     */
    fun setMove(canMove: Boolean): EntityBuilder {
        this.canMove = canMove
        return this
    }

    /**
     * Sets if the entity
     * cna pick up items
     * or not.
     *
     * @param canPickUpItems if true, entity can pick up items, false if not.
     * @return this.
     */
    fun setItemPickup(canPickUpItems: Boolean): EntityBuilder {
        this.canPickUpItems = canPickUpItems
        return this
    }

    /**
     * Sets the entity's name.
     *
     * @param name the name of the entity.
     * @return this.
     */
    fun setName(name: String?): EntityBuilder {
        this.name = name
        return this
    }
}