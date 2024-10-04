<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Edit Product</h2>
<form method="POST" action="${pageContext.request.contextPath}/product?action=edit&id=${product.id}">
    <div class="form-group">
        <label for="name">Product Name:</label>
        <input type="text" id="name" name="name" class="form-control" value="${product.name}" required>
    </div>
    <div class="form-group">
        <label for="price">Price:</label>
        <input type="number" id="price" name="price" class="form-control" step="0.01" min="0" value="${product.price}"
               required>
    </div>
    <button type="submit" class="btn btn-primary">Save Changes</button>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Back to List</a>
</form>
