namespace CultureXAPI.DTOs
{
    public class CountryDTO
    {

        public Guid Id { get; set; }
        public string Name { get; set; }
        public string CountryCode { get; set; }
        public string? FlagImageUrl { get; set; }
        public string? Description { get; set; }
        public string? Timezone { get; set; }
        public string? Currency { get; set; }
        public object? EmergencyContacts { get; set; }


    }
}
