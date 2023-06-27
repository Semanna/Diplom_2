package model;

public class CreateOrderResponse {
    private String name;
    private boolean success;
    private Order order;

    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CreateOrderResponse{" +
                "name='" + name + '\'' +
                ", success=" + success +
                ", order=" + order +
                '}';
    }

    public static class Order {
        private Integer number;

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "number=" + number +
                    '}';
        }
    }
}
