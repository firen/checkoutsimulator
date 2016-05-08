package eu.firen.checkoutsimulator;

import eu.firen.checkoutsimulator.checkout.CheckoutTransaction;
import eu.firen.checkoutsimulator.domain.Item;
import eu.firen.checkoutsimulator.loader.PricingRulesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Controller
@SpringBootApplication
public class CheckoutsimulatorApplication {
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	@Bean
	public CheckoutTransaction checkoutTransaction(HashMap<String, Item> items) {
		return new CheckoutTransaction(items);
	}

	@Autowired
	PricingRulesLoader pricingRulesLoader;

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(CheckoutsimulatorApplication.class, args);
		CheckoutsimulatorApplication bean = context.getBean(CheckoutsimulatorApplication.class);
		bean.run(args);
	}

	public void run(String... args) throws Exception {
		if(args.length != 1) {
			System.out.println("Program requires one parameter with path to CSV file with pricing rules");
		} else {
			System.out.println("Creating transaction with pricing rules in " + args[0]);
			Map items = pricingRulesLoader.load(args[0]);
			CheckoutTransaction checkoutTransaction = applicationContext.getBean(CheckoutTransaction.class, items);
			System.out.println("SUCCESS: Transaction created");
			try (Scanner scanner = new Scanner(System.in)) {
				while(true) {
					System.out.println("----------------");
					System.out.print("Enter SKU or type 'quit' to finish transaction: ");
					String sku = scanner.nextLine().toUpperCase();
					if("QUIT".equals(sku)) {
						System.out.println("Transaction finished. Thank you.");
						break;
					}
					if(checkoutTransaction.addItem(sku)) {
						System.out.println("SUCCESS: Item added with sku " + sku);
					} else {
						System.out.println("ERROR: Item does not exists with sku " + sku);
					}
					System.out.println("-------------------------------\nCurrent order:");
					checkoutTransaction.getPositions().stream()
							.forEach(position ->
									System.out.println(
											position.getQuantity()
											+ " " + position.getSku()
											+ " " + position.getPrice()
							));
					System.out.println("Total price: " + checkoutTransaction.calculateTotalPrice());
				}
			}
		}
	}
}
