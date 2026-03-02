package com.invadermonky.thaumicapi.api.impetus;

import thecodex6824.thaumicaugmentation.api.impetus.ImpetusStorage;

/**
 * An impetus (rift energy) storage is the unit of interaction with Impetus inventories.
 * <p>
 * A reference implementation can be found at {@link ImpetusStorage}.
 */
public interface IImpetusStorage {
    /**
     * Adds impetus to the storage. Returns the quantity of impetus that was accepted.
     *
     * @param maxReceive Maximum amount of impetus to be inserted.
     * @param simulate If TRUE, the insertion will only be simulated.
     * @return Amount of impetus that was (or would have been, if simulated) accepted by the storage.
     */
    int receiveImpetus(int maxReceive, boolean simulate);

    /**
     * Removes impetus from the storage. Returns quantity of impetus that was removed.
     *
     * @param maxExtract Maximum amount of impetus to be extracted.
     * @param simulate If TRUE, the extraction will only be simulated.
     * @return Amount of impetus that was (or would have been, if simulated) extracted from the storage.
     */
    int extractImpetus(int maxExtract, boolean simulate);

    /**
     * Returns the amount of impetus currently stored.
     */
    int getImpetusStored();

    /**
     * Returns the maximum amount of impetus that can be stored.
     */
    int getMaxImpetusStored();

    /**
     * Returns if this storage can have impetus extracted.
     * If this is false, then any calls to extractImpetus will return 0.
     */
    boolean canExtract();

    /**
     * Used to determine if this storage can receive impetus.
     * If this is false, then any calls to receiveImpetus will return 0.
     */
    boolean canReceive();
}
