package org.xandercat.pmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class BootstrapStackedProgressBarTest {

	@Test
	public void testComparisonBarWithGlobalMaximum() {
		BootstrapStackedProgressBar bar = new BootstrapStackedProgressBar(0, 10d);
		bar.addBar(3.2, "Min: ", "");
		bar.addBar(7.0, "Avg: ", "");
		bar.addBar(9.5, "Max", "");
		bar.finalized();
		BootstrapStackedProgressBar.Bar cbar = bar.getComparisonBar(9.5, "Test: ", "");
		assertNotNull(cbar);
		assertEquals(cbar.getPercent(), 0.95d, 0.001d);
	}

}
