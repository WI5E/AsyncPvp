package me.frostedsnowman.asyncpvp.api.damage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class DamageEvaluation {

    private double baseDamage;

    private int fireTicks;
    private double thornsDamage;

    private List<Runnable> internalVelocityChanges = new ArrayList<>();
    private List<Runnable> externalVelocityChanges = new ArrayList<>();

    private boolean criticalHit;
    private boolean sweep;

    private double finalDamage;

    @Nonnull
    public OptionalDouble getBaseDamage() {
        return this.baseDamage == 0 ? OptionalDouble.empty() : OptionalDouble.of(this.baseDamage);
    }

    @Nonnull
    public OptionalDouble getFinalDamage() {
        return this.finalDamage == 0 ? OptionalDouble.empty() : OptionalDouble.of(this.finalDamage);
    }

    @Nonnull
    public OptionalInt getFireTicks() {
        return this.fireTicks == 0 ? OptionalInt.empty() : OptionalInt.of(this.fireTicks);
    }

    @Nonnull
    public OptionalDouble getThornsDamage() {
        return this.thornsDamage == 0 ? OptionalDouble.empty() : OptionalDouble.of(this.thornsDamage);
    }

    public void addInternalVelocityChange(Runnable runnable) {
        this.internalVelocityChanges.add(runnable);
    }

    public void addExternalVelocityChange(Runnable runnable) {
        this.externalVelocityChanges.add(runnable);
    }
}
