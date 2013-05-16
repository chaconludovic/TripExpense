package com.eldoraludo.tripexpense;

import android.test.ActivityInstrumentationTestCase2;

/**
 * http://blogs.captechconsulting.com/blog/jairo-vazquez/android-unit-testing-
 * framework-overview
 * 
 * @author ludovic
 * 
 */
public class DepenseCalculatorTest extends
		ActivityInstrumentationTestCase2<SyntheseActivity> {

	private SyntheseActivity syntheseActivity;

	public DepenseCalculatorTest() {
		super("com.eldoraludo.tripexpense", SyntheseActivity.class);
	}

	public DepenseCalculatorTest(Class<SyntheseActivity> activityClass) {
		super(activityClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		syntheseActivity = this.getActivity();

	}

	public void testPreconditions() {

	}

	public void testText() {
		
	}
}