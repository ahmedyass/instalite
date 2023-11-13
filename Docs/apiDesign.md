# API Design

## Visitor

- `GET /api/public/images` : Retrieve all public images.
- `GET /api/public/images/{imageId}` : Retrieve a specific public image.
- `GET /api/public/images/{imageId}/comments` : Retrieve comments for a specific image.

## User

- `POST /api/public/images/{imageId}/comments`: Add a comment to an image.
- `PUT /api/public/comments/{commentId}`: Edit a comment.
- `DELETE /api/public/comments/{commentId}`: Delete a comment.
- `PUT /api/users/{userId}` : Edit user profile.

## Privileged User

- `POST /api/private/images/{imageId}/comments`: Add a comment to an image.
- `PUT /api/private/comments/{commentId}`: Edit a comment.
- `DELETE /api/private/comments/{commentId}`: Delete a comment.

## Administrator

(can view/edit/delete all comments, images, users)

- `POST /api/images`: Upload a new image.
- `PUT /api/images/{imageId}`: Update an image.
- `DELETE /api/images/{imageId}`: Delete an image.
- `GET /api/users/{userId}`: Retrieve a user's profile.
- `DELETE /api/users/{userId}`: Delete a user.
