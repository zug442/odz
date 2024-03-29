 package com.company;
import java.util.*;
import java.text.*;
/**
 * Containing items and calculating price.
 */
public class ShoppingCart
{
 private static final int MAX_LENGTH_TITLE = 32;
 private static final int MIN_LENGTH_TITLE = 0;
 private static final float MIN_PRICE = 0.1f;
 private static final float MIN_QUANTITY = 1;
 private static final float EMPTY_CART = 0;

 public static enum ItemType { NEW, REGULAR, SECOND_FREE, SALE };
 /**
  * Tests all class methods.
  */
 public static void main(String[] args)
 {
  // TODO: add tests here
  ShoppingCart cart = new ShoppingCart();
  cart.addItem("Apple", 0.99, 5, ItemType.NEW);
  cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
  cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
  cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
  System.out.println(cart.formatTicket());
 }
 /**
  * Adds new item.
  *
  * @param title item title 1 to 32 symbols
  * @param price item ptice in USD, > 0
  * @param quantity item quantity, from 1
  * @param type item type
  *
  * @throws IllegalArgumentException if some value is wrong
  */
 public void addItem(String title, double price, int quantity, ItemType type)
 {
  if (title == null || title.length() == MIN_LENGTH_TITLE || title.length() > MAX_LENGTH_TITLE)
   throw new IllegalArgumentException("Illegal title");

  if (price < MIN_PRICE)
   throw new IllegalArgumentException("Illegal price");

  if (quantity < MIN_QUANTITY)
   throw new IllegalArgumentException("Illegal quantity");

  Item item = new Item();
  item.title = title;
  item.price = price;
  item.quantity = quantity;
  item.type = type;

  items.add(item);
 }
 private void frameBuilder(String footer[], int align[], int width[], StringBuilder sb) {
  for (int i = 0; i < footer.length; i++)
  appendFormatted(sb, footer[i], align[i], width[i]);
 }

 private void separator(StringBuilder sb, int lineLength) {
  for (int i = 0; i < lineLength; i++)
   sb.append("-");
  sb.append("\n");
 }

 private class FormattingPreparation {
  private String[] header;
  private List<String[]> lines;
  private String[] footer;
  public FormattingPreparation() {
  }

  public void addLines() {
   FormattingPreparation fP = new FormattingPreparation();
   fP.header = new String[]{"#","Item","Price","Quan.","Discount","Total"};
   int index = 0;
   for (Item item : items) {
    fP.lines.add(new String[]{
            String.valueOf(++index),
            item.title,
            MONEY.format(item.price),
            String.valueOf(item.quantity),
            (calculateDiscount(item.type, item.quantity) == 0) ? "-" : (calculateDiscount(item.type, item.quantity) + "%"),
            MONEY.format(calculateItemTotal(item, calculateDiscount(item.type, item.quantity)))
    });
    fP.footer = new String[] { String.valueOf(index),"","","","",
            MONEY.format(calculateTotalPrice(item)) };
   }
  }

  private double calculateItemTotal(Item item, int discount) {
   return item.price * item.quantity * (100.00 - discount) / 100.00;
  }

  private double calculateTotalPrice(Item item) {
   double total = 0.00;
   total += calculateItemTotal(item, calculateDiscount(item.type, item.quantity));
   return total;
  }
 }
 /**
  * Formats shopping price.
  *
  * @return string as lines, separated with \n,
  * first line: # Item Price Quan. Discount Total
  * second line: ---------------------------------------------------------
  * next lines: NN Title $PP.PP Q DD% $TT.TT
  * 1 Some title $.30 2 - $.60
  * 2 Some very long $100.00 1 50% $50.00
  * ...
  * 31 Item 42 $999.00 1000 - $999000.00
  * end line: ---------------------------------------------------------
  * last line: 31 $999050.60
  *
  * if no items in cart returns "No items." string.
  */
 public String formatTicket()
 {
  FormattingPreparation formattingPreparation = new FormattingPreparation();
  String[] header = {"#","Item","Price","Quan.","Discount","Total"};
  int[] align = new int[] { 1, -1, 1, 1, 1, 1 };
  if (items.size() == EMPTY_CART)
   return "No items.";
  // formatting each line
  formattingPreparation.addLines();

  // column max length
  int[] width = new int[]{0,0,0,0,0,0};
  for (String[] line : formattingPreparation.lines)
   for (int i = 0; i < line.length; i++)
    width[i] = Math.max(width[i], line[i].length());

  // line length
  int lineLength = width.length - 1;
  for (int w : width)
   lineLength += w;

  StringBuilder sb = new StringBuilder();
  sb.append("\n");
  // header
  frameBuilder(header,align,width,sb);
  // separator
  separator(sb,lineLength);
  // lines
  for (String[] line : formattingPreparation.lines) {
   frameBuilder(line,align,width,sb);
   sb.append("\n");
  }

  if (formattingPreparation.lines.size() > 0) {
   // separator
   separator(sb, lineLength);
  }
  // footer
  frameBuilder(formattingPreparation.footer,align,width,sb);

  return sb.toString();
 }
 // --- private section -----------------------------------------------------
 private static final NumberFormat MONEY;
 static {
  DecimalFormatSymbols symbols = new DecimalFormatSymbols();
  symbols.setDecimalSeparator('.');
  MONEY = new DecimalFormat("$#.00", symbols);
 }

 /**
  * Appends to sb formatted value.
  * Trims string if its length > width.
  * @param align -1 for align left, 0 for center and +1 for align right.
  */
 public static void appendFormatted(StringBuilder sb, String value, int align, int width)
 {
  if (value.length() > width)
   value = value.substring(0,width);
  int before = (align == 0)
          ? (width - value.length()) / 2
          : (align == -1) ? 0 : width - value.length();
  int after = width - value.length() - before;
  while (before-- > 0)
   sb.append(" ");
  sb.append(value);
  while (after-- > 0)
   sb.append(" ");
  sb.append(" ");
 }
 /**
  * Calculates item's discount.
  * For NEW item discount is 0%;
  * For SECOND_FREE item discount is 50% if quantity > 1
  * For SALE item discount is 70%
  * For each full 10 not NEW items item gets additional 1% discount,
  * but not more than 80% total
  */
 public static int calculateDiscount(ItemType type, int quantity)
 {
  int discount = 0;
  switch (type) {
   case NEW:
    return 0;

   case REGULAR:
    discount = 0;
    break;

   case SECOND_FREE:
    if (quantity > 1)
     discount = 50;
    break;
   case SALE:
    discount = 70;
    break;
  }
  if (discount < 80) {
   discount += quantity / 10;
   if (discount > 80)
    discount = 80;
  }

  return discount;
 }
 /** item info */
 public static class Item
 {
      String title;
      double price;
      int quantity;
      ItemType type;
 }

 /** Container for added items */
 private List<Item> items = new ArrayList<Item>();
}