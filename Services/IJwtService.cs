using CultureXAPI.Models;

namespace CultureXAPI.Services
{
    public interface IJwtService
    {

        string GenerateAccessToken(User user);
        string GenerateRefreshToken();
        bool ValidateToken(string token);
        Guid GetUserIdFromToken(string token);

    }
}
