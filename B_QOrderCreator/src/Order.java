import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class Order {

	String APIKEY = "***REMOVED***";
	ArrayList<String> quanity,sku,price,tax;
	String orderNumber;
	Customer customer;
	String salesOrderNumber;


	public Order(ArrayList<String> quanity, ArrayList<String> sku, ArrayList<String> price,
			ArrayList<String> tax, String orderNumber, String salesOrderNumber, Customer customer) {
		super();
		this.quanity = quanity;
		this.sku = sku;
		this.price = price;
		this.tax = tax;
		this.orderNumber = orderNumber;
		this.customer = customer;
		this.salesOrderNumber = salesOrderNumber;
	}
	public void upload()
	{
		uploadOrder("15223925");
	}
	public String uploadCustomer()
	{

		Client client = ClientBuilder.newClient();
		Entity payload = Entity.json("{  \"customer\": { \"email\": \"diy.com\",    \"phone\": \""+customer.phone+"\",    \"mobile\": \""+customer.mobile+"\",    \"billing_address_attributes\": {      \"first_name\": \"B&Q\",      \"last_name\": \"\",      \"company\": \"B&Q plc\",      \"address1\": \"B&Q House\",      \"address2\": \"Chestnut Avenue\",      \"city\": \"Southampton\",      \"country\": \"UK\",      \"zip\": \"SO53 3LE\"    }  }}");	
		Response response = client.target("https://api.veeqo.com/customers")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.post(payload);

		String body = response.readEntity(String.class);

		String customerID = body.substring(body.indexOf("id") + 4,body.indexOf(","));
		System.out.println("customer id " + customerID);

		return customerID;
	}
	public void uploadOrder(String customerID)
	{

		Client client = ClientBuilder.newClient();
		String lineItemAttributes = "";
		for(int i =0 ; i < sku.size() -1; i ++)
		{

			lineItemAttributes +=  "            {\r\n" + 
					"                \"quantity\": "+quanity.get(i)+",\r\n" + 
					"                \"sellable_id\": "+convertToSellableID(sku.get(i))+",\r\n" + 
					"                \"price_per_unit\": "+price.get(i)+",\r\n" + 
					"                \"tax_rate\": "+tax.get(i)+"\r\n" + 
					"            },\r\n";
		}
		lineItemAttributes +=  "            {\r\n" + 
				"                \"quantity\": "+quanity.get(quanity.size() -1)+",\r\n" + 
				"                \"sellable_id\": "+convertToSellableID(sku.get(quanity.size() -1))+",\r\n" + 
				"                \"price_per_unit\": "+price.get(quanity.size() -1)+",\r\n" + 
				"                \"tax_rate\": "+tax.get(quanity.size() -1)+"\r\n" + 
				"            }\r\n";
		Entity payload = Entity.json("\r\n" + 
				"{\r\n" + 
				"    \"order\": {\r\n" + 
				"        \"channel_id\": 48307,\r\n" + 
				"        \"customer_id\":"+ customerID + ",\r\n" + 
				"        \"deliver_to_attributes\": {\r\n" + 
				"            \"address1\": \""+customer.addr1+"\",\r\n" + 	
				"            \"address2\": \""+customer.addr2+"\",\r\n" + 
				"            \"city\": \""+customer.city+"\",\r\n" + 
				"            \"company\": \""+customer.company+"\",\r\n" + 
				"            \"country\": \""+customer.country+"\",\r\n" + 
				"            \"customer_id\": 7014549,\r\n" + 
				"            \"first_name\": \""+customer.firstName+"\",\r\n" + 
				"            \"last_name\": \""+customer.lastName+"\",\r\n" + 
				"            \"phone\": \""+customer.phone+"\",\r\n" + 
				"            \"state\": \""+customer.state+"\",\r\n" + 
				"            \"zip\": \""+customer.zip+"\"\r\n" + 
				"        },\r\n" + 
				"        \"line_items_attributes\": [\r\n" + 
				lineItemAttributes +
				"        ],\r\n" +
				"\"customer_note_attributes\": {\r\n" +
				"        \"text\": \""+orderNumber + " " + salesOrderNumber + "\"\r\n" + 
				"      }\r\n" + 
				"    }\r\n" + 
				"}");
		Response response = client.target("https://api.veeqo.com/orders")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.post(payload);

		System.out.println(payload.getEntity().toString());
		//System.out.println(response.readEntity(String.class));
	}

	public String convertToSellableID(String sku)
	{

		Client client = ClientBuilder.newClient();
		Response response = client.target("https://api.veeqo.com/products?query=" + sku)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.get();
		String body = response.readEntity(String.class);
		body = body.substring(body.indexOf("\"sku_code\":\"" + sku + "\""));
		String sellableID = body.substring(body.indexOf("sellable_id") + 13,body.indexOf(",",body.indexOf(("sellable_id"))));
		System.out.println("sellable id " + sellableID);
		return sellableID;

	}

	public String getShippingID(String customerID)
	{
		Client client = ClientBuilder.newClient();
		Response response = client.target("https://api.veeqo.com/customers/" + customerID)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.get();
		String body = response.readEntity(String.class);
		//9477189
		String shippingAddress = body.substring(body.indexOf("billing_address"));
		String shippingID = shippingAddress.substring(shippingAddress.indexOf("id") + 4,shippingAddress.indexOf(",",shippingAddress.indexOf(("id"))));

		System.out.println("shipping id " + shippingID);

		return shippingID;
	}





}
