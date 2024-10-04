<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Delete Product</h2>
<form method="POST" action="${pageContext.request.contextPath}/product?action=delete&id=${product.id}">
    <input type="hidden" name="id" value="${product.id}"/>

    <div class="form-group">
        <p>Are you sure you want to delete the product <strong>${product.name}</strong>?</p>
    </div>

    <button type="submit" class="btn btn-danger">Delete</button>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Back to List</a>
</form>
