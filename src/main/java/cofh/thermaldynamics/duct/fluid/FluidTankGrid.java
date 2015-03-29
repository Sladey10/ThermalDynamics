package cofh.thermaldynamics.duct.fluid;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.MathHelper;

import gnu.trove.map.hash.TObjectIntHashMap;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankGrid extends FluidTankAdv {

	public int fluidThroughput = 120;
	public int fluidPerDuct = 3000;
	public FluidGrid myMaster;
	static TObjectIntHashMap<String> fluidFlowrate = new TObjectIntHashMap<String>();

	static {
		fluidFlowrate.put("steam", 600);
	}

	public FluidTankGrid(int capacity, FluidGrid theGrid) {

		super(capacity);
		myMaster = theGrid;
	}

	@Override
	public void setFluid(FluidStack fluid) {

		if (fluid != null) {
			setFluidData(fluid);
		}
		super.setFluid(fluid);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (fluid == null && resource != null) {
			setFluidData(resource);
		}
		return super.fill(resource, doFill);
	}

	public void setFluidData(FluidStack fluid) {

		int viscosity = 0;

		if (fluid != null) {
			viscosity = FluidRegistry.getFluid(fluid.fluidID).getViscosity();
			fluidThroughput = MathHelper.clampI(120000 / viscosity, 80, 600);

			if (fluidFlowrate.containsKey(fluid.getFluid().getName())) {
				fluidThroughput = fluidFlowrate.get(fluid.getFluid().getName());
			}
			fluidThroughput *= myMaster.getThroughPutModifier();
			fluidPerDuct = Math.min(25 * fluidThroughput, myMaster.getMaxFluidPerDuct());
		}
		myMaster.fluidChanged();
	}

}
