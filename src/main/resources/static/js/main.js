$(document).ready(function () {

    //modal form for change password
    $('.edit-login').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function (user, status) {
            $('.edit-login-form #email').val(user.userEmail);
        })

        $('.edit-login-form #userUpdateModal').modal();
    })

    //form for editing contact info,fetches selected phone number info from /findPhone/{id} , and pre-fills the form
    $('.editPhoneBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function (phone, status) {
            $('.phone-form #phoneId').val(phone.phoneID);
            $('.phone-form #phone-num').val(phone.phoneNum);
            $('.phone-form #ModalLabel').html("Edit contact number");
        })

        $('.phone-form #phoneUpdateModal').modal();
    })

    //adding new contact number
    $('.addPhoneBtn').on('click', function (event) {
        event.preventDefault();
        $('.phone-form #ModalLabel').html("Add contact number");
        $('.phone-form #phoneId').val(null);
        $('.phone-form #phone-num').val(null);

        $('.phone-form #phoneUpdateModal').modal();
    })

    //edit address , fetches selected address info from /findAddress/{id} , and pre-fills the form
    $('.editAddressBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function (address, status) {
            $('.address-form #addressId').val(address.addressId);
            $('.address-form #street-address').val(address.street);
            $('.address-form #city').val(address.city);
            $('.address-form #state').val(address.state);
            $('.address-form #zip').val(address.zip);
            $('.address-form #addressModalLabel').html("Edit address");
        })

        $('.address-form #addressUpdateModal').modal();
    })

    //add new address
    $('.addAddressBtn').on('click', function (event) {
        event.preventDefault();
        $('.address-form #addressModalLabel').html("Add address");
        $('.address-form #addressId').val(null);
        $('.address-form #street-address').val(null);
        $('.address-form #city').val(null);
        $('.address-form #state').val(null);
        $('.address-form #zip').val(null);

        $('.address-form #addressUpdateModal').modal();
    })

    //Edit product , fetches selected product info from /findProduct/{id} , and pre-fills the form
    $('.editProductBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function (product, status) {
            $('.product-form #productId').val(product.productId);
            $('.product-form #product-name').val(product.productName);
            $('.product-form #product-price').val(product.productPrice);
            $('.product-form #product-desc').val(product.productDesc);
            $('.product-form #product-stock').val(product.quantity);
            $('.product-form #product-category').val(product.category);
            $('.product-form #ModalLabel').html("Edit product");
        })

        $('.product-form #productUpdateModal').modal();
    })

    //Add new product form
    // TO DO still : upload photo with matching name as product id
    $('.addProductBtn').on('click', function (event) {
        event.preventDefault();

        $('.product-form #productId').val(null);
        $('.product-form #product-name').val(null);
        $('.product-form #product-price').val(null);
        $('.product-form #product-desc').val(null);
        $('.product-form #product-stock').val(null);
        $('.product-form #product-category').val(null);
        $('.product-form #ModalLabel').html("Add product");

        $('.product-form #productUpdateModal').modal();
    })

    //shows a modal with product details fetched from /findProduct/{id}
    $('.product-info-btn').on('click', function (event) {
        event.preventDefault();
        //gets the filename / id from the card component ex. data-id="1.jpg" in the <a>
        var myImageId = $(this).data('id');
        var href = $(this).attr('href');
        $.get(href, function (product, status) {
            $('.product-info-modal #productId').val(product.productId);
            $('.product-info-modal   #productImg').attr("src", "/images/"+myImageId+".jpg");
            $('.product-info-modal   #productImg').css("width", "60%");
            $('.product-info-modal   .productDesc').text(product.productDesc);
            $('.product-info-modal   .productPrice').text("$"+product.productPrice);

        })

        // $('.product-info-modal .carouselExampleSlidesOnly').carousel({
        //     interval: 2000
        // })

        $('.product-info-modal #productInfoModal').modal();
    })





    /* Set the width of the side navigation to 250px */
    function openNav() {
        document.getElementById("mySidenav").style.width = "250px";
    }

    /* Set the width of the side navigation to 0 */
    function closeNav() {
        document.getElementById("mySidenav").style.width = "0";
    }
})