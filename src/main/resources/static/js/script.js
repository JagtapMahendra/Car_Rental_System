const paymentStart = () => {
    console.log("Payment started...");

    let amountText = $("#totalAmount").text();
    let amount = parseFloat(amountText.replace('₹', '').trim());

    if (!amount || amount <= 0) {
        alert("Amount must be greater than zero.");
        return;
    }

    // Set the total amount in the hidden input field before submitting
    $("#totalAmountInput").val(amount);

    let carId = $("#carId").val();
    let userId = $("#userId").val();
    let pickupDate = $("#pickupDate").val();
    let returnDate = $("#returnDate").val();
    let driverLicenseNumber = $("input[name='driverLicenseNumber']").val();
    let driverName = $("input[name='driverName']").val();
    let address = $("textarea[name='address']").val();
    let idProofImage = $("input[name='idProofImage']")[0].files[0];

    if (!carId || !userId || !pickupDate || !returnDate || !driverLicenseNumber || !driverName || !address || !idProofImage) {
        alert("Please fill all required fields and upload the ID proof.");
        return;
    }

    $.ajax({
        url: '/user/create_order',
        data: JSON.stringify({ amount: amount, info: 'order_request' }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function (response) {
            if (response.status === 'created') {
                let options = {
                    "key": "rzp_test_oaCUW6qWKCoV15",
                    "amount": response.amount,
                    "currency": "INR",
                    "name": "CAR RENT",
                    "description": "Car Rental Payment",
                    "image": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRayVGYC55LYRKiIhbgxWjGxFQ53bK93MZZ2g&s",
                    "order_id": response.id,
                    handler: function (paymentResponse) {
                        console.log("Payment Success:", paymentResponse.razorpay_payment_id);

                        let formData = new FormData($("form")[0]);
                        formData.append("razorpayPaymentId", paymentResponse.razorpay_payment_id);

                        $.ajax({
                            url: '/booking/confirm',
                            type: 'POST',
                            data: formData,
                            processData: false,
                            contentType: false,
                            success: function () {
                                window.location.href = "/booking/confirmation";
                            },
                            error: function () {
                                alert("Booking confirmation failed. Please contact support.");
                            }
                        });
                    },
                    prefill: {
                        "name": "Customer Name",
                        "email": "customer@example.com",
                        "contact": "9999999999"
                    },
                    notes: {
                        "address": "Razorpay Corporate Office"
                    },
                    "theme": {
                        "color": "#3399cc"
                    }
                };
                var rzp1 = new Razorpay(options);
                rzp1.open();
            }
        },
        error: function () {
            alert("Something went wrong. Please try again.");
        }
    });
};
