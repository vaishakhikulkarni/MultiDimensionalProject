/*
 * @author Vaishakhi kulkarni
 * Net Id: vpk140230
 * */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.*;
import java.util.*;

public class Project3 {
	static int[] categories;
	static final int NUM_CATEGORIES = 1000, MOD_NUMBER = 997;
	static int DEBUG = 9;
	private int phase = 0;
	private long startTime, endTime, elapsedTime;

	public static TreeMap<Long, cust> customer = new TreeMap<Long, cust>();
	public static HashMap<Integer, TreeSet<cust>> topThree = new HashMap<Integer, TreeSet<cust>>();

	public static void main(String[] args) throws FileNotFoundException {
		categories = new int[NUM_CATEGORIES];
		Scanner in;
		if (args.length > 0) {
			in = new Scanner(new File(args[0]));
		} else {
			in = new Scanner(System.in);
		}
		Project3 x = new Project3();
		x.timer();
	
		long rv = x.driver(in);
		System.out.println(rv);
		x.timer();
	}

	/**
	 * Read categories from in until a 0 appears. Values are copied into static
	 * array categories. Zero marks end.
	 * 
	 * @param in
	 *            : Scanner from which inputs are read
	 * @return : Number of categories scanned
	 */
	public static int readCategories(Scanner in) {
		int cat = in.nextInt();
		int index = 0;
		while (cat != 0) {
			categories[index++] = cat;
			cat = in.nextInt();
		}
		categories[index] = 0;
		return index;
	}

	public long driver(Scanner in) {
		String s;
		long rv = 0, id;
		int cat;
		double purchase;

		while (in.hasNext()) {
			s = in.next();
			if (s.charAt(0) == '#') {
				s = in.nextLine();
				continue;
			}
			if (s.equals("Insert")) {
				id = in.nextLong();
				readCategories(in);
				rv += insert(id, categories);
			} else if (s.equals("Find")) {
				id = in.nextLong();
				rv += find(id);
			} else if (s.equals("Delete")) {
				id = in.nextLong();
				rv += delete(id);
			} else if (s.equals("TopThree")) {
				cat = in.nextInt();
				rv += topthree(cat);
			} else if (s.equals("AddInterests")) {
				id = in.nextLong();
				readCategories(in);
				rv += addinterests(id, categories);
			} else if (s.equals("RemoveInterests")) {
				id = in.nextLong();
				readCategories(in);
				rv += removeinterests(id, categories);
			} else if (s.equals("AddRevenue")) {
				id = in.nextLong();
				purchase = in.nextDouble();
				rv += addrevenue(id, purchase);
			} else if (s.equals("Range")) {
				double low = in.nextDouble();
				double high = in.nextDouble();
				rv += range(low, high);
			} else if (s.equals("SameSame")) {
				rv += samesame();
			} else if (s.equals("NumberPurchases")) {
				id = in.nextLong();
				rv += numberpurchases(id);
			} else if (s.equals("End")) {
				return rv % 997;
			} else {
				System.out
						.println("Houston, we have a problem.\nUnexpected line in input: "
								+ s);
				System.exit(0);
			}
		}
		// This can be inside the loop, if overflow is a problem
		rv = rv % MOD_NUMBER;
		return rv;
	}

	public void timer() {
		if (phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			memory();
			phase = 0;
		}
	}

	public void memory() {
		long memAvailable = Runtime.getRuntime().totalMemory();
		long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		System.out.println("Memory: " + memUsed / 1000000 + " MB / "
				+ memAvailable / 1000000 + " MB.");
	}

	public class cust implements Comparable<cust> {
		Long id;
		HashSet<Integer> category = new HashSet<Integer>();
		double amount;
		int count;
		int seen;

		cust(long uniqueid, HashSet<Integer> catego, double amt, int cnt) {
			id = uniqueid;
			category = catego;
			amount = amt;
			count = cnt;
			seen =0;
		}

		cust() {
			id = (long) 0;
			category.clear();
			amount = 0;
			count = 0;
			seen =0;
		}

		@Override
		public int compareTo(cust c1) { // TODO Auto-generated

			if (this.amount < c1.amount)
				return -1;
			else if (this.amount > c1.amount)
				return 1;
			else
				return 0;
		}
	}

	// Perform insertion in the customer data structure which a treeMap
	int insert(long id, int[] categories) {

		Long ids = new Long(id); // Wrapper function
		HashSet<Integer> tempcategory = new HashSet<Integer>();

		// Check whether Customer Id is already present.If not add in the data
		// structure
		if (!customer.containsKey(ids)) {

			for (int i = 0; i < categories.length && categories[i] != 0; i++) {

				tempcategory.add(categories[i]);
			}

			// Create a new object and insert in the data structure we have
			// created to store
			cust x = new cust(ids, tempcategory, 0, 0);
			customer.put(ids, x);

			// Data structure to perform topThree operation
			for (int i = 0; i < categories.length && categories[i] != 0; i++) {

				if (!topThree.containsKey(categories[i])) {

					TreeSet<cust> idSet = new TreeSet<cust>();
					idSet.add(x);
					topThree.put(categories[i], idSet);
				} else {
					topThree.get(categories[i]).add(x);
				}
			}
			return 1;
		} else
			// If already present return -1
			return -1;
	}

	// To find whether particular Id is present.If present return amount
	// associated with that customer Id
	int find(long id) {

		Long ids = new Long(id); // wrapper function

		if (customer.containsKey(id)) {

			cust findRecord = customer.get(ids);
			double amt = findRecord.amount;
			return (int) amt;
		} else
			// If not present return -1
			return -1;
	}

	// To delete the record of particular customer which has customer id as id
	int delete(long id) {

		Long ids = new Long(id);
		// If present then delete it from the topThree and customer data
		// structure created by us.Return the amount associated with that
		// customer Id
		if (customer.containsKey(ids)) {

			cust deleterecord = customer.get(ids);
			double amount = deleterecord.amount;
			int num = 0;
			HashSet<Integer> catelist = deleterecord.category;
			Iterator<Integer> itr = catelist.iterator();

			while (itr.hasNext()) {

				num = itr.next();
				TreeSet<cust> removeid = topThree.get(num);

				if (removeid.contains(deleterecord))
					removeid.remove(deleterecord);
				topThree.replace(num, removeid);
			}

			customer.remove(ids);
			return (int) amount;
		} else
			// Return -1 if not present
			return -1;
	}

	// Perform top three operation
	// given a category k, find the top three customers
	// (in terms of amount spent) who are interested in category k
	int topthree(int cat) {

		Integer cats = new Integer(cat);
		double amt = 0;
		int size = 3;
		// If present returns the sum of the amounts of the top three
		// customers.Truncate the dollar amount
		if (topThree.containsKey(cats)) {
			TreeSet<cust> tempTop = topThree.get(cats);

			if (tempTop.size() < 3)
				size = tempTop.size();

			Iterator<cust> treeItr = tempTop.descendingIterator();

			while (treeItr.hasNext() && size > 0) {

				cust c = treeItr.next();
				amt = amt + c.amount;
				size--;
			}

			return (int) amt;
		} else
			// else return -1
			return -1;
	}

	// Add new interests to the list of
	// a customer's categories. Some of them may already be in the
	// list of categories of this customer.
	int addinterests(long id, int[] catego) {
		Long ids = new Long(id); // Wrapper class

		// Return the number of new
		// categories added to that customer's record and make changes in
		// customer and topthree data structure created by us
		if (customer.containsKey(ids)) {

			cust categorylist = customer.get(ids);
			int i = 0;
			int count = 0;

			while (i < catego.length && catego[i] != 0) {

				if (!categorylist.category.contains(catego[i])) {

					categorylist.category.add(catego[i]);
					count++;
				}
				i++;
			}
			customer.replace(ids, categorylist);

			if (count != 0) { // TopThree data structure to perform updates
				int j = 0;
				while (j < catego.length && catego[j] != 0) {

					if (!topThree.containsKey(catego[j])) {
						TreeSet<cust> custSet = new TreeSet<cust>();
						custSet.add(categorylist);
						topThree.put(categories[j], custSet);
					} else {
						topThree.get(catego[j]).remove(categorylist);
						topThree.get(catego[j]).add(categorylist);
					}
					j++;
				}
			}
			return count;
		} else
			// return -1 customer id is not already present
			return -1;
	}

	// Remove some categories from the
	// list of categories associated with a customer. Return the
	// number of categories left in the customer's record.
	int removeinterests(long id, int[] categor) {

		Long ids = new Long(id);
		int flag = 0;
		// Make updates in customer data structure which store all the details
		// of the customer
		if (customer.containsKey(ids)) {
			cust removeinterest = customer.get(ids);
			if (removeinterest.category != null) {
				for (int i = 0; i < categor.length && categor[i] != 0; i++) {
					if (removeinterest.category.contains(categor[i])) {
						removeinterest.category.remove(categor[i]);
						flag = 1;
					}
				}
			}
			// Make updates in topThree data structure created for us which
			// store the category id corresponding to customers
			if (flag == 1) {
				customer.replace(ids, removeinterest); // check once
				for (int i = 0; i < categor.length && categor[i] != 0; i++) {
					if (topThree.containsKey(categor[i])) {
						if (topThree.get(categor[i]).contains(removeinterest))
							topThree.get(categor[i]).remove(removeinterest);
					}
				}
			}
			return removeinterest.category.size(); // return the size of the
													// category list after
													// performing remove
													// operation
		} else
			// If already is not present return -1
			return -1;
	}

	int addrevenue(long id, double purchase) {
		Long ids = new Long(id);

		if (!customer.containsKey(ids)) // if customer id is not present return
										// -1
			return -1;
		else {

			cust addrevenue = customer.get(ids);
			int category;

			HashSet<Integer> categorySet = addrevenue.category;
			Iterator<Integer> categorySetItr = categorySet.iterator();

			while (categorySetItr.hasNext()) {

				category = categorySetItr.next();
				TreeSet<cust> removecustId = topThree.get(category);
				removecustId.remove(addrevenue);
			}
			// perform addition ofamount
			addrevenue.amount = addrevenue.amount + purchase;
			addrevenue.count = addrevenue.count + 1;

			Iterator<Integer> categorySetItr1 = categorySet.iterator(); // iterator

			while (categorySetItr1.hasNext()) {

				category = categorySetItr1.next();
				TreeSet<cust> custId = topThree.get(category);
				custId.add(addrevenue);
			}

			return (int) addrevenue.amount;
		}

	}

	// functio to number of customers whose amount is at least
	// "low" and at most "high".
	int range(double low, double high) {

		int cnt = 0;

		Set<Long> keys = customer.keySet();

		for (Iterator<Long> i = keys.iterator(); i.hasNext();) {

			Long key = (Long) i.next();
			cust temp = customer.get(key);

			// check whether it is in a given range
			if (temp.amount >= low && temp.amount <= (high + 0.0001)) {
				cnt++;
			}

		}
		return cnt; // return no of customers present in given range
	}

	int samesame() {

		int count = 0;
		Set<Long> keys = customer.keySet();
		
		for (Long key : keys) {
				
				cust temp = customer.get(key);
				
				HashSet<Integer> check = temp.category;
				
				for(Long keycheck : keys)
				{			
					if(key!=keycheck && check.size()>=5)
					{
						cust tempcheck = customer.get(keycheck);
					
						if(tempcheck.category.size()== check.size() && tempcheck.category.containsAll(check) && temp.seen==0)
						{
							count ++;
							temp.seen = 1;
						}
						
					}
				}
			}
		
		for(Long key : keys)
		{
			cust c = customer.get(key);
			c.seen=0;
		}
		
		return count;
	}

	// Function he number of times customer has purchased
	// products (i.e., number of calls to AddRevenue for this customer).
	int numberpurchases(long id) {

		Long ids = new Long(id);
		// check if particular customer id is present.And if yes return number
		// of
		// times update is performed related with amount
		if (customer.containsKey(ids)) {

			cust number = customer.get(ids);
			return number.count;
		} else
			// if not present return -1
			return -1;
	}

}
