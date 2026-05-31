CREATE TABLE transport
{
    id SERIAL PRIMARY KEY,
      transport_name VARCHAR(255) NOT NULL,
      transport_type VARCHAR(50) NOT NULL,
      estimated_travel_time VARCHAR(50) NOT NULL,
      description TEXT
};