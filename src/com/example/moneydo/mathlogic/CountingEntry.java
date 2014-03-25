package com.example.moneydo.mathlogic;

import java.util.ArrayList;
import java.util.List;

public class CountingEntry extends MathEntry {
	
	private List<Float> mLowerList = new ArrayList<Float>();
	
	protected CountingEntry(EntryType inType) {
		super(inType);
	}
	public void LowerAmount(float delta) {
		SetAmount(GetAmount()-delta);
		mLowerList.add(delta);
	}
	@Override
	public void SetAmount(float inAmount) {
		for (float lower : mLowerList) {
			inAmount-=lower;
		}
		super.SetAmount(inAmount);
	}
}
