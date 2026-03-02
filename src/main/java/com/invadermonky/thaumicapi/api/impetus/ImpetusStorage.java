package com.invadermonky.thaumicapi.api.impetus;

/**
 * Reference implementation of {@link IImpetusStorage}. Use/extend this or implement your own.
 */
public class ImpetusStorage implements IImpetusStorage {
    protected int stored;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public ImpetusStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.stored = energy;
    }

    public ImpetusStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public ImpetusStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public ImpetusStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    @Override
    public int receiveImpetus(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int impetusReceived = Math.min(this.capacity - this.stored, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            this.stored += impetusReceived;
        return impetusReceived;
    }

    @Override
    public int extractImpetus(int maxExtract, boolean simulate) {
        if(!this.canExtract())
            return 0;

        int impetusExtracted = Math.min(this.stored, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            this.stored -= impetusExtracted;
        return impetusExtracted;
    }

    @Override
    public int getImpetusStored() {
        return this.stored;
    }

    @Override
    public int getMaxImpetusStored() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }
}
